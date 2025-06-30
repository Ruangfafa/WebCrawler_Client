package org.ruangfafa;

import org.openqa.selenium.WebDriver;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Crawlers.SellerCrawler;
import org.ruangfafa.Service.DatabaseService;
import org.ruangfafa.Service.Logger;

import java.sql.Connection;

import static org.ruangfafa.Service.DatabaseService.*;


public class Application {
    private static final Connection DB = DatabaseService.getConnection();
    private static final WebDriver driver = ChromeDriver.createWebDriver("user1");
    public static void main(String[] args) {
        if (DB == null) return;
        while (true) {
            int serverState = getState(DB,true);
            if (serverState == -1) {
                Logger.log("🛑 检测到 Server 状态为 -1，退出程序", "Application.java");
                break;
            }
            int userState = getState(DB,false);


            while (userState == 2 && serverState == 0) {
                String taskUrl = popUrl(DB);
                if (taskUrl == null) {
                    taskDone(DB);
                    break;
                }
                insertSeller(DB,SellerCrawler.craw(driver, taskUrl, DB));
            }

            while (userState == 3 && serverState == 0) {
                String taskUrl = popUrl(DB);
                //第三部分爬虫
                if (taskUrl == null) {
                    taskDone(DB);
                    break;
                }
            }

            while (userState == 4 && serverState == 0) {
                String taskUrl = popUrl(DB);
                //第四部分爬虫
                if (taskUrl == null) {
                    taskDone(DB);
                    break;
                }
            }

            while (userState == 6 && serverState == 0) {
                String taskUrl = popUrl(DB);
                //第六部分爬虫
                if (taskUrl == null) {
                    taskDone(DB);
                    break;
                }
            }

            try {
                Thread.sleep(5000); // 每次循环间隔5秒（可根据需要调整）
            } catch (InterruptedException e) {
                Logger.log("⚠️ 主线程休眠被中断: " + e.getMessage(), "Application.java");
                break;
            }
        }
    }
}

