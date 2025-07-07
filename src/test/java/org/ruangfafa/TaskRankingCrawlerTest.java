package org.ruangfafa;

import org.openqa.selenium.WebDriver;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Crawlers.TaskRankingCrawler;
import org.ruangfafa.Service.DatabaseService;

import java.sql.Connection;

public class TaskRankingCrawlerTest {
    private static final Connection DB = DatabaseService.getConnection();
    private static WebDriver driver = ChromeDriver.createWebDriver("user1");

    public static void main(String[] args) {
        //TaskRankingCrawler.craw(driver,"https://huodong.taobao.com/wow/z/tbhome/tbpc-venue/rank?tagId=97966127",DB);
        TaskRankingCrawler.craw(driver,"https://huodong.taobao.com/wow/z/tbhome/tbpc-venue/rank?tagId=102554916",DB);

    }
}
