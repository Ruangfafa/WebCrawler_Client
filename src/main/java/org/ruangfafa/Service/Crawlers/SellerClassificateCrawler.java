package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ruangfafa.Model.Classificate;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Identifiers.PageIdentify;
import org.ruangfafa.Service.Logger;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ruangfafa.Service.AbnormalProcessing.solveSliderCaptcha;
import static org.ruangfafa.Service.AbnormalProcessing.urlSupervision;
import static org.ruangfafa.Service.DatabaseService.insertSellerClassificate;

public class SellerClassificateCrawler {
    public static void craw(WebDriver driver, String url, Connection DB) {
        String pageType = "", identifier = "", category_pv = "", cName = "";
        driver.get(url);
        Matcher matcher;
        ChromeDriver.waitForPageLoad(driver, DB);
        urlSupervision(driver, url);
        solveSliderCaptcha(driver);
        pageType = PageIdentify.searchPageIdentify(driver);
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

                // 主分类处理
                try {
                    List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class,'attrValues')]/ul[contains(@class, 'av-collapse') and contains(@class, 'd5c')]//li"));
                    for (WebElement item : items) {
                        try {
                            WebElement link = item.findElement(By.xpath(".//a"));
                            String href = link.getAttribute("href").trim();
                            category_pv = "N/A";

                            // 提取 category-XXXXXXXXX
                            Matcher m = Pattern.compile("category-(\\d+)\\.htm").matcher(href);
                            if (m.find()) {
                                category_pv = "c_:_" + m.group(1);
                            }
                            cName = link.getText().trim();
                            if (cName.isEmpty()) {
                                cName = (String) ((JavascriptExecutor) driver)
                                        .executeScript("return arguments[0].textContent.trim();", link);
                            }
                            if (!category_pv.equals("N/A"))
                                insertSellerClassificate(DB, new Classificate(pageType, identifier, category_pv, cName));
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }

                // 属性分类处理
                try {
                    List<WebElement> props = driver.findElements(By.xpath("//div[contains(@class, 'propAttrs')]//div[contains(@class, 'attr') and contains(@class, 'J_TProp')]"));
                    for (WebElement prop : props) {
                        try {
                            WebElement keyElement = prop.findElement(By.xpath(".//div[contains(@class, 'attrKey')]"));
                            String categoryName = keyElement.getText().trim();
                            if (categoryName.isEmpty()) {
                                categoryName = (String) ((JavascriptExecutor) driver).executeScript(
                                        "return arguments[0].textContent.trim();", keyElement
                                );
                            }

                            List<WebElement> optionLinks = prop.findElements(By.xpath(".//div[contains(@class, 'attrValues')]//ul[contains(@class, 'av-collapse')]//li//a"));
                            for (WebElement link : optionLinks) {
                                try {
                                    String href = link.getAttribute("href").trim();
                                    category_pv = "N/A";

                                    // 提取 pv=XXXXXXXX
                                    Matcher m = Pattern.compile("pv=([^&]+)").matcher(href);
                                    if (m.find()) {
                                        category_pv = "p_:_" + m.group(1);
                                    }

                                    String text = link.getText().trim();
                                    if (text.isEmpty()) {
                                        text = (String) ((JavascriptExecutor) driver).executeScript(
                                                "return arguments[0].textContent.trim();", link
                                        );
                                    }

                                    cName = categoryName + "_:_" + text;
                                    insertSellerClassificate(DB, new Classificate(pageType, identifier, category_pv, cName));
                                } catch (Exception ignored) {
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
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
    }
}
