package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ruangfafa.Model.Product;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Identifiers.PageIdentify;


import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ruangfafa.Service.AbnormalProcessing.driverBlocker;
import static org.ruangfafa.Service.AbnormalProcessing.urlSupervision;
import static org.ruangfafa.Service.DatabaseService.insertProduct;

public class ProductCrawler {
    public static void craw(WebDriver driver, String url, Connection DB){
        // 初始化空字段
        String pageType = "", id = "", skuid = "", orgnPrice = "", diskPrice = "", title = "", soldAmount365 = "",
                storageAddress = "", guarantee = "", pattern = "", storageLeft = "", parameter = "",
                patternCom = "", image = "", imageSet = "";
        Pattern patternForm;
        driver.get(url);

        ChromeDriver.waitForPageLoad(driver, DB);
        driverBlocker(driver);
        pageType = PageIdentify.classificatePageIdentify(driver);

        switch (pageType) {
            case "tm":
                try {
                    // 正则提取 id 参数值
                    patternForm = Pattern.compile("[?&]id=(\\d+)");
                    Matcher matcher = patternForm.matcher(url);
                    if (matcher.find()) {
                        id = matcher.group(1); // 第一个括号捕获组
                    } else {
                        id = "N/A";
                    }
                } catch (Exception e) {
                    id = "N/A";
                }

                try {
                    // 正则提取 skuId 参数值
                    patternForm = Pattern.compile("[?&]skuId=(\\d+)");
                    Matcher matcher = patternForm.matcher(url);
                    if (matcher.find()) {
                        skuid = matcher.group(1);
                    } else {
                        skuid = "N/A";
                    }
                } catch (Exception e) {
                    skuid = "N/A";
                }

                try {
                    org.openqa.selenium.WebElement priceWrap = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[@class='pageContentWrap']//div[contains(@class,'content')]//div[@id='purchasePanel']//div[contains(@class,'displayPrice')]//div[contains(@class,'priceWrap')]"
                    ));
                    try {
                        org.openqa.selenium.WebElement origPrice = priceWrap.findElement(By.xpath(
                                ".//div[contains(@class,'subPrice')]//span[contains(@class,'text')][2]"
                        ));
                        org.openqa.selenium.WebElement discPrice = priceWrap.findElement(By.xpath(
                                ".//div[contains(@class,'highlightPrice')]//span[contains(@class,'text')]"
                        ));
                        orgnPrice = origPrice.getText().replace("¥", "").replace("￥", "").replace("起", "").trim();
                        diskPrice = discPrice.getText().replace("¥", "").replace("￥", "").replace("起", "").trim();
                    } catch (Exception e) {
                        org.openqa.selenium.WebElement origPrice = priceWrap.findElement(By.xpath(
                                ".//div[contains(@class,'highlightPrice')]//span[contains(@class,'text')]"
                        ));
                        orgnPrice = origPrice.getText().replace("¥", "").replace("￥", "").replace("起", "").trim();
                        diskPrice = "No Discount";
                    }
                } catch (Exception e) {
                    orgnPrice = "N/A";
                    diskPrice = "N/A";
                }

                try {
                    WebElement titleElement = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[contains(@class,'contentWrap')]//div[contains(@class,'ItemTitle')]//h1"
                    ));
                    title = titleElement.getText().trim();
                } catch (Exception e) {
                    title = "N/A";
                }

                try {
                    WebElement soldAmountElement = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[@class='pageContentWrap']//div[contains(@class,'content')]//div[@id='purchasePanel']//div[contains(@class,'displayPrice')]//div[contains(@class,'salesDesc')]"
                    ));
                    soldAmount365 = soldAmountElement.getText()
                            .replace("已售 ", "")
                            .replace("·", "")
                            .trim();
                } catch (Exception e) {
                    soldAmount365 = "N/A";
                }

                try {
                    WebElement storageElement = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[@class='pageContentWrap']//div[contains(@class,'content')]//div[@id='purchasePanel']//div[contains(@class,'delivery-from-addr')]"
                    ));
                    storageAddress = storageElement.getText().replace("至", "").trim();
                } catch (Exception e) {
                    storageAddress = "N/A";
                }

                try {
                    WebElement triggerWrap = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[@class='pageContentWrap']//div[contains(@class,'content')]//div[@id='purchasePanel']//div[contains(@class,'guaranteeInfo')]//div[contains(@class,'triggerWrap')]"
                    ));
                    List<WebElement> guarantees = triggerWrap.findElements(
                            By.xpath(".//span[contains(@class,'guaranteeText')]")
                    );
                    List<String> guaranteeList = new ArrayList<>();
                    for (WebElement item : guarantees) {
                        String text = item.getText().trim();
                        if (!text.isEmpty()) {
                            guaranteeList.add(text);
                        }
                    }
                    if (!guaranteeList.isEmpty()) {
                        guarantee = String.join("&&", guaranteeList);
                    } else {
                        guarantee = "None";
                    }
                } catch (Exception e) {
                    guarantee = "N/A";
                }

                try {
                    WebElement skuContent = driver.findElement(
                            By.xpath("//div[contains(@class,'SkuContent')]")
                    );

                    List<WebElement> patternClasses = skuContent.findElements(
                            By.xpath(".//div[contains(@class,'skuItem')]")
                    );

                    List<String> patternClassList = new ArrayList<>();

                    for (WebElement patternClass : patternClasses) {
                        try {
                            // 例如 “颜色”
                            WebElement patternClassName = patternClass.findElement(
                                    By.xpath(".//span[contains(@class,'labelText')]")
                            );
                            String patternClassNameText = patternClassName.getText().trim();

                            // 选中的具体值，例如 “蓝色”
                            WebElement selected = patternClass.findElement(
                                    By.xpath(".//div[contains(@class,'isSelected')]//span")
                            );
                            String isSelectedText = selected.getText().trim();

                            if (!patternClassNameText.isEmpty() && !isSelectedText.isEmpty()) {
                                patternClassList.add(patternClassNameText + ":" + isSelectedText);
                            }
                        } catch (Exception inner) {
                            // 某个 patternClass 下没选中项就跳过
                        }
                    }

                    if (!patternClassList.isEmpty()) {
                        pattern = String.join("||", patternClassList);
                    } else {
                        pattern = "None";
                    }
                } catch (Exception e) {
                    pattern = "N/A";
                }

                storageLeft = "<NDY!>";

                try {
                    WebElement tableWrapper = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[contains(@class,'main')]//div[contains(@class,'BasicContent')]//div[contains(@class,'tabDetailWrap')]//div[contains(@class,'baseDropsInfo')]//div[contains(@class,'tableWrapper')]"
                    ));

                    List<WebElement> parameterTypes = tableWrapper.findElements(
                            By.xpath(".//div[contains(@class,'infoItem')]")
                    );

                    List<String> paramList = new ArrayList<>();

                    for (WebElement parameterItem : parameterTypes) {
                        try {
                            WebElement parameterKey = parameterItem.findElement(By.xpath(".//div[contains(@class,'infoItemTitle')]"));
                            String parameterKeyText = parameterKey.getText().trim();

                            WebElement parameterVal = parameterItem.findElement(By.xpath(".//div[contains(@class,'infoItemContent')]"));
                            String parameterValText = parameterVal.getText().trim();

                            if (!parameterKeyText.isEmpty() && !parameterValText.isEmpty()) {
                                paramList.add(parameterKeyText + ":" + parameterValText);
                            }
                        } catch (Exception ignoreOneParam) {
                            // 单个参数块异常跳过
                        }
                    }

                    if (!paramList.isEmpty()) {
                        parameter = String.join("&&", paramList);
                    } else {
                        parameter = "None";
                    }

                } catch (Exception e) {
                    parameter = "N/A";
                }

                try {
                    WebElement tagList = driver.findElement(By.xpath(
                            "//div[@id='ice-container']//div[contains(@class,'main')]//div[contains(@class,'BasicContent')]//div[contains(@class,'tabDetailWrap')]//div[contains(@class,'Comments')]//div[contains(@class,'tagList')]"
                    ));

                    List<WebElement> commentTypes = tagList.findElements(
                            By.xpath(".//span[contains(@class,'tagItem')]")
                    );

                    StringBuilder sb = new StringBuilder();
                    for (WebElement commentType : commentTypes) {
                        String text = commentType.getText().trim();
                        if (!text.isEmpty()) {
                            if (sb.length() > 0) sb.append("&&");
                            sb.append(text);
                        }
                    }

                    patternCom = sb.toString().isEmpty() ? "None" : sb.toString();
                } catch (Exception e) {
                    patternCom = "N/A";
                }

                image = imageSet = "<NDY!>";

                try {
                    WebElement skuContent = driver.findElement(By.xpath("//div[contains(@class,'SkuContent')]"));
                    List<List<WebElement>> patternClassesList = new ArrayList<>();

                    List<WebElement> patternClasses = skuContent.findElements(By.xpath("./div[contains(@class,'skuItem')]"));
                    for (WebElement patternClass : patternClasses) {
                        List<WebElement> options = patternClass.findElements(By.xpath(".//div[contains(@class,'valueItem')]"));
                        if (!options.isEmpty()) {
                            patternClassesList.add(options);
                        }
                    }

                    if (patternClassesList.isEmpty()) {
                        insertProduct(DB, new Product(pageType, id, skuid, orgnPrice, diskPrice, title,
                                soldAmount365, storageAddress, guarantee, pattern, storageLeft,
                                parameter, patternCom, image, imageSet));
                        break;
                    }

                    int[] currentIndices = new int[patternClassesList.size()];
                    boolean hasNext = true;

                    while (hasNext) {
                        boolean valid = true;
                        for (int i = 0; i < currentIndices.length; i++) {
                            WebElement item = patternClassesList.get(i).get(currentIndices[i]);
                            String cls = item.getAttribute("class");
                            if (cls.contains("isDisabled")) {
                                valid = false;
                                break;
                            }
                        }

                        if (valid) {
                            // 点击组合
                            for (int i = 0; i < currentIndices.length; i++) {
                                WebElement item = patternClassesList.get(i).get(currentIndices[i]);
                                String cls = item.getAttribute("class");
                                if (!cls.contains("isSelected")) {
                                    item.click();
                                    Thread.sleep(800);
                                }
                            }

                            // 抓取点击后 pattern
                            List<String> patternList = new ArrayList<>();
                            for (int i = 0; i < currentIndices.length; i++) {
                                WebElement option = patternClassesList.get(i).get(currentIndices[i]);
                                String text = option.getText().trim();
                                if (!text.isEmpty()) patternList.add(text);
                            }
                            pattern = patternList.isEmpty() ? "None" : String.join("||", patternList);

                            try {
                                String curUrl = driver.getCurrentUrl();  // 获取当前点击组合后变化后的链接
                                patternForm = Pattern.compile("[?&]skuId=(\\d+)");
                                Matcher matcher = patternForm.matcher(curUrl);
                                if (matcher.find()) {
                                    skuid = matcher.group(1);
                                } else {
                                    skuid = "N/A";
                                }
                            } catch (Exception e) {
                                skuid = "N/A";
                            }

                            // 抓取点击后价格
                            try {
                                WebElement priceWrap = driver.findElement(By.xpath(
                                        "//div[@id='ice-container']//div[contains(@class,'priceWrap')]"));
                                WebElement discPriceElem = priceWrap.findElement(By.xpath(
                                        ".//div[contains(@class,'highlightPrice')]//span[contains(@class,'text')]"));
                                diskPrice = discPriceElem.getText().replace("¥", "").replace("￥", "").replace("起", "").trim();
                                try {
                                    WebElement origPriceElem = priceWrap.findElement(By.xpath(
                                            ".//div[contains(@class,'subPrice')]//span[contains(@class,'text')][2]"));
                                    orgnPrice = origPriceElem.getText().replace("¥", "").replace("￥", "").replace("起", "").trim();
                                } catch (Exception ex) {
                                    orgnPrice = diskPrice;
                                }
                            } catch (Exception e) {
                                orgnPrice = "N/A";
                                diskPrice = "N/A";
                            }

                            // 插入组合数据
                            insertProduct(DB, new Product(pageType, id, skuid, orgnPrice, diskPrice, title,
                                    soldAmount365, storageAddress, guarantee, pattern, storageLeft,
                                    parameter, patternCom, image, imageSet));
                        }

                        // 推进组合
                        for (int i = currentIndices.length - 1; i >= 0; i--) {
                            currentIndices[i]++;
                            if (currentIndices[i] < patternClassesList.get(i).size()) {
                                break;
                            } else {
                                currentIndices[i] = 0;
                                if (i == 0) hasNext = false;
                            }
                        }
                    }

                } catch (Exception e) {
                    pattern = "N/A";
                    insertProduct(DB, new Product(pageType, id, skuid, orgnPrice, diskPrice, title,
                            soldAmount365, storageAddress, guarantee, pattern, storageLeft,
                            parameter, patternCom, image, imageSet));
                }

                break;

            case "tb_c2c_1":
            case "tb_c2c_2":
            case "tb_c2c_ice":
            case "tm_global":
            case "tb_global":
            case "jd_zy_922474":
            case "jd_zy_401022":
            case "jd_fs_922474":
                // 这些页面暂不处理，保留 switch-case 结构以便未来拓展
                return;
        }


    }
}
