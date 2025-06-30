package org.ruangfafa;

import org.openqa.selenium.WebDriver;
import org.ruangfafa.Model.Seller;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Crawlers.SellerCrawler;
import org.ruangfafa.Service.DatabaseService;

import java.sql.Connection;

public class SellerCrawlerTest {
    public static void main(String[] args) {
        WebDriver driver = ChromeDriver.createWebDriver("user1");
        Connection DB = DatabaseService.getConnection();
        Seller test= SellerCrawler.craw(driver,"https://greenconnection.tmall.com/search.htm",DB);
        System.out.println(test);
    }

}
