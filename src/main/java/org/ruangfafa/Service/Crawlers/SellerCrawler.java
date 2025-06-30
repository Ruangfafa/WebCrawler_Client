package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ruangfafa.Model.Seller;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Identifiers.PageIdentify;
import org.ruangfafa.Service.Logger;

import java.lang.runtime.SwitchBootstraps;
import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SellerCrawler {
    public static Seller craw(WebDriver driver, String url, Connection DB){
        String identifier = "", name = "", location = "", pageType = "", subscribe = "", qualityScore = "", garenteeScore = "", logisticsScore = "";
        driver.get(url);
        ChromeDriver.waitForPageLoad(driver, DB);
        while (!url.equals(driver.getCurrentUrl())) {
            if (driver.getCurrentUrl().contains("login.taobao.com")) {
                try {
                    WebElement loginBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".fm-button.fm-submit")));
                    loginBtn.click();
                } catch (Exception e) {
                    Logger.log("⚠️ 登录按钮点击失败: " + e.getMessage(), "LoginHandler.java");
                }
            }
        }
        Matcher matcher;
        Actions actions = new Actions(driver);
        List<WebElement> scoreElements;

        pageType = PageIdentify.searchPageIdentify(driver);
        switch (pageType) {
            case "tb_c2c_1":
                name = driver.findElement(By.xpath(".//a[contains(@class, 'shop-name-link')]")).getText().trim(); //$x(".//a[contains(@class, 'shop-name-link')]")[0]?.textContent.trim()
                matcher = Pattern.compile("shop(\\d+)\\.taobao\\.com").matcher(url);
                identifier = matcher.find() ? matcher.group(1) : "N/A";
                scoreElements = driver.findElements(
                        By.xpath("//div[contains(@class,'shop-service-info')]//li[contains(@class,'shop-service-info-item')]//span[contains(@class,'rateinfo')]/em")
                );
                location = subscribe = qualityScore = garenteeScore = logisticsScore = "N/A";
                if (scoreElements.size() == 3) {
                    qualityScore = scoreElements.get(0).getText().trim();
                    garenteeScore = scoreElements.get(1).getText().trim();
                    logisticsScore = scoreElements.get(2).getText().trim();
                }
                break;
            case "tb_c2c_2":
                name = (String) ((JavascriptExecutor) driver).executeScript(
                        "let el = arguments[0]; return el.childNodes[0]?.textContent.trim();",
                        driver.findElement(By.xpath(".//a[contains(@class, 'J_TGoldlog')]"))
                ); //$x(".//a[contains(@class, 'J_TGoldlog')]/text()")[0]?.textContent.trim()
                matcher = Pattern.compile("shop(\\d+)\\.taobao\\.com").matcher(url);
                identifier = matcher.find() ? matcher.group(1) : "N/A";
                scoreElements = driver.findElements(
                        By.xpath("//a[contains(@class,'mini-dsr') and contains(@class,'J_TGoldlog')]//span[contains(@class,'dsr-num')]")
                );
                location = subscribe = qualityScore = garenteeScore = logisticsScore = "N/A";
                if (scoreElements.size() == 3) {
                    qualityScore = scoreElements.get(0).getText().trim();
                    garenteeScore = scoreElements.get(1).getText().trim();
                    logisticsScore = scoreElements.get(2).getText().trim();
                }
                break;
                case "tb_c2c_ice":
                    name = driver.findElement(By.xpath(".//div[contains(@class, 'shopName--')]")).getText().trim(); //$x(".//div[contains(@class, 'shopName--')]")[0]?.textContent.trim()
                    matcher = Pattern.compile("shop(\\d+)\\.taobao\\.com").matcher(url);
                    identifier = matcher.find() ? matcher.group(1) : "N/A";
                    subscribe = driver.findElement(By.xpath("//div[contains(@class, 'fans--hensgUT0')]")).getText().replace("粉丝", "").trim();

                    List<WebElement> scoreSpans = new ArrayList<>();
                    for (int i = 0; i < 6; i++) {
                        try {
                            actions.moveToElement(driver.findElement(By.xpath("//div[contains(@class,'pannerContainer--')]"))).perform();
                            scoreSpans = new WebDriverWait(driver, Duration.ofMillis(500))
                                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                                            By.xpath("//span[contains(@class, 'dsrScore')]")
                                    ));
                            if (scoreSpans.size() >= 3) break;
                        } catch (Exception ignored) {}
                    }
                    location = qualityScore = logisticsScore = garenteeScore = "N/A";
                    if (scoreSpans.size() >= 3) {
                        qualityScore = scoreSpans.get(0).getText().trim();
                        logisticsScore = scoreSpans.get(1).getText().trim();
                        garenteeScore = scoreSpans.get(2).getText().trim();
                    }
                    break;
            case "tm": case "tm_global":
                name = driver.findElement(By.xpath(".//a[contains(@class, 'slogo-shopname')]/strong")).getText().trim(); //$x(".//a[contains(@class, 'shop-name-link')]")[0]?.textContent.trim()
                matcher = Pattern.compile("https://([^.]+)\\.tmall\\.com").matcher(url);
                identifier = matcher.find() ? matcher.group(1) : "N/A";
                location = "N/A";
                By xpath = By.xpath("//li[contains(@class,'locus')]//div[contains(@class,'right')]");

                for (int i = 0; i < 6; i++) {
                    try {
                        actions.moveToElement(driver.findElement(By.xpath("//div[contains(@class,'main-info')]"))).perform();
                        location = new WebDriverWait(driver, Duration.ofMillis(500))
                                .until(ExpectedConditions.visibilityOfElementLocated(xpath)).getText().trim();
                        break;
                    } catch (Exception ignored) {}
                }
                scoreElements = driver.findElements(
                    By.xpath("//span[contains(@class, 'shopdsr-score-con')]")
                );
                subscribe = qualityScore = garenteeScore = logisticsScore = "N/A";
                if (scoreElements.size() == 3) {
                    qualityScore = scoreElements.get(0).getText().trim();
                    garenteeScore = scoreElements.get(1).getText().trim();
                    logisticsScore = scoreElements.get(2).getText().trim();
                }
                break;
            case "jd_zy_922474": case "jd_zy_401022":
                name = driver.findElement(By.xpath("//div[contains(@class, 'jLogo')]/a")).getText().trim();
                matcher = Pattern.compile("view_search-(\\d+)").matcher(url);
                identifier = matcher.find() ? matcher.group(1) : "N/A";
                location = subscribe = qualityScore = garenteeScore = logisticsScore = "N/A";
                break;
            case "jd_fs_922474":
                name = driver.findElement(By.xpath("//div[contains(@class, 'jLogo')]/a")).getText().trim();
                matcher = Pattern.compile("view_search-(\\d+)").matcher(url);
                identifier = matcher.find() ? matcher.group(1) : "N/A";
                location = subscribe = "N/A";
                scoreElements = driver.findElements(
                        By.xpath("//tbody[contains(@id, 'jRatingTotal_table')]//span[contains(@class,'level-text-red')]")
                );
                qualityScore = garenteeScore = logisticsScore = "N/A";
                if (scoreElements.size() >= 3) {
                    qualityScore = scoreElements.get(0).getAttribute("textContent").trim();   // 用户评价
                    logisticsScore = scoreElements.get(2).getAttribute("textContent").trim(); // 物流履约
                    garenteeScore = scoreElements.get(4).getAttribute("textContent").trim();  // 售后服务
                }

            default:
                break;
        }


        return new Seller(identifier, name, location, pageType, subscribe, qualityScore, garenteeScore, logisticsScore);
    }
}
