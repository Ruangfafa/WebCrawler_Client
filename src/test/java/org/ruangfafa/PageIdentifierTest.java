package org.ruangfafa;

import org.openqa.selenium.WebDriver;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.DatabaseService;
import org.ruangfafa.Service.Identifiers.PageIdentify;

import java.sql.Connection;

import static org.ruangfafa.Service.AbnormalProcessing.urlSupervision;
import static org.ruangfafa.Service.ChromeDriver.waitForPageLoad;

public class PageIdentifierTest {
    public static void main(String[] args) {
        WebDriver driver = ChromeDriver.createWebDriver("user1");
        Connection DB = DatabaseService.getConnection();
        String url;
        //driver.get("https://shop527244726.taobao.com/search.htm");
        //driver.get("https://shop211698093.taobao.com/search.htm");
        //driver.get("https://shop152348667.taobao.com/search.htm");
        //driver.get("https://greenconnection.tmall.com/search.htm");
        //driver.get("https://babyglobal.tmall.hk/search.htm");
        //driver.get("https://mall.jd.com/view_search-7328302.html");
        //driver.get("https://mall.jd.com/view_search-405960.html");
        //driver.get("https://mall.jd.com/view_search-1449089.html");
        url = "https://greenconnection.tmall.com/category-1601811475.htm";
        driver.get(url);
        waitForPageLoad(driver,DB);
        urlSupervision(driver,url);
        System.out.println(PageIdentify.searchPageIdentify(driver));
    }
}
