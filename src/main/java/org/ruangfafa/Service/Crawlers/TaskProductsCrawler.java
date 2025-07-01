package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.ruangfafa.Model.Classificate;
import org.ruangfafa.Model.ProductTag;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Identifiers.PageIdentify;

import java.sql.Connection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ruangfafa.Service.AbnormalProcessing.urlSupervision;
import static org.ruangfafa.Service.DatabaseService.insertProductTag;
import static org.ruangfafa.Service.DatabaseService.insertSellerClassificate;

public class TaskProductsCrawler {
    public static void craw(WebDriver driver, String url, Connection DB) {
        String pageType = "", identifier = "", id = "", soldAmount = "", tag = "";
        driver.get(url);
        ChromeDriver.waitForPageLoad(driver, DB);
        urlSupervision(driver,url);
        Matcher matcher;
        try{Thread.sleep(1000);}catch(Exception _){}
        pageType = PageIdentify.classificatePageIdentify(driver);
        switch (pageType) {
            case "tb_c2c_1":
                break;
            case "tb_c2c_2":
                break;
            case "tb_c2c_ice":
                break;
            case "tm":
                matcher = Pattern.compile("https://([^.]+)\\.tmall\\.com").matcher(url);
                identifier = matcher.find() ? matcher.group(1) : "N/A";
                try {
                    WebElement tagElement = driver.findElement(By.xpath(
                            "//a[contains(@class,'crumbStrong') and contains(@class,'crumbDrop-hd') and contains(@class,'J_TCrumbDropHd')]"
                    ));
                    tag = tagElement.getText().trim();
                } catch (Exception e) {
                    tag = "N/A";
                    System.out.println("⚠️ 未找到 tag 元素，设置为 N/A");
                }
                while (true) {
                    //在div class="J_TItems"中会包含若干div class="item4line1"，每个div class="item4line1"里又包含若干类似 dl class="item " data-id="940022115982",每个dl里又会包含一个span class="sale-num"，拿到这个span的gettext，保存到soldAmount，data-id保存到id
                    // 获取所有 item4line1 区块
                    List<WebElement> itemBlocks = driver.findElements(By.xpath("//div[@class='J_TItems']//div[contains(@class,'item4line1')]"));

                    for (WebElement block : itemBlocks) {
                        // 每个 block 下的所有商品 dl 元素
                        List<WebElement> items = block.findElements(By.xpath(".//dl[contains(@class,'item ')]"));

                        for (WebElement item : items) {
                            try {
                                // 获取 data-id
                                String itemId = item.getAttribute("data-id").trim();

                                // 获取销量
                                WebElement saleNumElement = item.findElement(By.xpath(".//span[contains(@class,'sale-num')]"));
                                String saleText = saleNumElement.getText().trim();

                                // 存入变量，供后续写入数据库
                                id = itemId;
                                soldAmount = saleText;

                                insertProductTag(DB, new ProductTag(pageType, identifier, id, soldAmount, tag));
                            } catch (Exception e) {
                                System.out.println("⚠️ 跳过一个商品，原因：" + e.getMessage());
                            }
                        }
                    }
                    insertProductTag(DB, new ProductTag(pageType, identifier, id, soldAmount, tag));
                    try {
                        WebElement pagination = driver.findElement(By.className("pagination"));
                        List<WebElement> links = pagination.findElements(By.tagName("a"));

                        boolean isLastPage = false;
                        for (WebElement link : links) {
                            if (link.getText().contains("下一页") && link.getAttribute("class").contains("disable")) {
                                isLastPage = true;
                                break;
                            }
                        }

                        if (isLastPage) break;

                        // 获取页码输入框
                        WebElement pageInput = driver.findElement(By.xpath("//input[@name='pageNo']"));
                        int currentPage = Integer.parseInt(pageInput.getAttribute("value").trim());
                        int nextPage = currentPage + 1;

                        // 清空并输入新页码
                        pageInput.clear();
                        pageInput.sendKeys(String.valueOf(nextPage));

                        // 点击确定按钮
                        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit' and contains(text(),'确定')]"));
                        submitButton.click();

                        // 等待加载（可替换为 WebDriverWait）
                        ChromeDriver.waitForPageLoad(driver, DB);
                        Thread.sleep(500);

                    } catch (Exception e) {
                        System.out.println("⚠️ 翻页异常，退出分页循环");
                        break;
                    }
                }
                break;
            case "tb_global":
                break;
            case "jd_zy_922474":
                break;
            case "jd_zy_401022":
                break;
            case "jd_fs_922474":
                break;
        }


        insertProductTag(DB,new ProductTag(pageType,identifier,id,soldAmount,tag));
    }
}
