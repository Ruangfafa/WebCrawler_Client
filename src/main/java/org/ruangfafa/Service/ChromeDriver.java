package org.ruangfafa.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeDriver {

    // 获取 chromedriver 路径
    private static String getChromedriverPath() {
        URL chromedriverUrl = ChromeDriver.class.getClassLoader().getResource("chrome_chromedriver_137.0.7151.119/chromedriver.exe");
        if (chromedriverUrl == null) {
            throw new RuntimeException("无法找到 chromedriver 文件");
        }
        return new File(chromedriverUrl.getFile()).getAbsolutePath();
    }

    // 获取 Chrome 浏览器路径
    private static String getChromeBinaryPath() {
        URL chromeBinaryUrl = ChromeDriver.class.getClassLoader().getResource("chrome_chromedriver_137.0.7151.119/chrome.exe");
        if (chromeBinaryUrl == null) {
            throw new RuntimeException("无法找到 chrome 浏览器文件");
        }
        return new File(chromeBinaryUrl.getFile()).getAbsolutePath();
    }

    // 创建并返回 WebDriver
    public static WebDriver createWebDriver(String user) {
        // 获取 chromedriver 和 chrome 的路径
        String chromedriverPath = getChromedriverPath();
        String chromeBinaryPath = getChromeBinaryPath();

        // 设置 chromedriver 路径
        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        // 设置 ChromeOptions 指定浏览器路径
        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromeBinaryPath);

        // ✅ 设置用户数据目录（必须是绝对路径，且该目录下必须有已有登录记录）
        // 比如你自己在某次用 Chrome 登录淘宝后，user-data-dir 的目录就记录了 session/cookie
        options.addArguments("--user-data-dir="+System.getProperty("user.dir")+"/chrome_profiles/"+user);

        // ✅ 可选：指定某个 Profile，比如 "Profile 1"，而不是默认 Profile
        // options.addArguments("--profile-directory=Profile 1");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        // 启动并返回 WebDriver 实例
        return new org.openqa.selenium.chrome.ChromeDriver(options);
    }

    public static void waitForPageLoad(WebDriver driver, int timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete")
        );
    }
}
