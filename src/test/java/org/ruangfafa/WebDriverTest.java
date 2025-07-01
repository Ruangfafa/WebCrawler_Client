package org.ruangfafa;

import org.openqa.selenium.WebDriver;
import org.ruangfafa.Service.ChromeDriver;

public class WebDriverTest {
    public static void main(String[] args) {
        WebDriver driver = ChromeDriver.createWebDriver("user1");

        driver.get("https://shop527244726.taobao.com/search.htm");
        driver.get("https://shop211698093.taobao.com/search.htm");
        driver.get("https://shop152348667.taobao.com/search.htm");
        driver.get("https://greenconnection.tmall.com/search.htm");
        driver.get("https://babyglobal.tmall.hk/search.htm");
        driver.get("https://mall.jd.com/view_search-7328302.html");
        driver.get("https://mall.jd.com/view_search-405960.html");
        driver.get("https://mall.jd.com/view_search-1449089.html");
    }
}
