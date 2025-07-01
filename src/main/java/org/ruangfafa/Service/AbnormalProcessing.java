package org.ruangfafa.Service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
}
