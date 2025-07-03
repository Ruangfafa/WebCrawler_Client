package org.ruangfafa.Service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AbnormalProcessing {
    public static void urlSupervision(WebDriver driver, String url) {
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
    }

    public static void driverBlocker(WebDriver driver) {
        while (true) {
            try {
                List<?> dialog1 = driver.findElements(By.className("baxia-dialog-content"));
                List<?> dialog2 = driver.findElements(By.xpath("//div[contains(@class, 'mui-dialog') and contains(@class, 'mui-dialog-hasmask')]"));
                if (!dialog1.isEmpty()) {
                    driver.quit();
                    driver = ChromeDriver.createWebDriver("user1");
                }
                else if (!dialog2.isEmpty()) {
                    driver.quit();
                    driver = ChromeDriver.createWebDriver("user1");
                }
                else {
                    return;
                }

            } catch (Exception e) {
                System.out.println("⚠️ 检测 driverBlocker 出现异常：" + e.getMessage());
            }
        }
    }

    public static void solveSliderCaptcha(WebDriver driver) {
        try {
            System.out.println("🛑 检测到滑块验证，请手动完成后等待自动继续...");

            // 持续检测滑块是否仍存在（每1秒检测一次，最多等180秒）
            int maxWaitSeconds = 180;
            for (int i = 0; i < maxWaitSeconds; i++) {
                List<WebElement> sliders = driver.findElements(By.id("no1_n1z"));
                if (sliders.isEmpty()) {
                    System.out.println("✅ 滑块验证已完成，继续执行任务...");
                    return;
                }
                Thread.sleep(1000);
            }

            System.out.println("⚠️ 等待滑块验证超时，请检查是否成功验证");
        } catch (Exception e) {
            System.out.println("⚠️ 等待滑块验证时异常: " + e.getMessage());
        }
    }


}
