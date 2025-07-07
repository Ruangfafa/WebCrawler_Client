package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ruangfafa.Model.ProductTag;
import org.ruangfafa.Model.RankingProduct;
import org.ruangfafa.Service.AbnormalProcessing;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Identifiers.PageIdentify;

import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ruangfafa.Service.AbnormalProcessing.driverBlocker;
import static org.ruangfafa.Service.DatabaseService.insertProductTag;
import static org.ruangfafa.Service.DatabaseService.insertTaskRanking;

public class TaskRankingCrawler {
    public static void craw(WebDriver driver, String url, Connection DB) {
        String rankName = "", id = "", rank = "", pageType = "";
        driver.get(url);
        ChromeDriver.waitForPageLoad(driver, DB);
        driverBlocker(driver);
        Matcher matcher;
        Pattern patternForm;
        try{Thread.sleep(4000);}catch(Exception _){}
        pageType = PageIdentify.rankPageIdentify(driver);
        switch (pageType) {
            case "tb_c2c_1":
                break;
            case "tb_c2c_2":
                break;
            case "tb_c2c_ice":
                break;
            case "tm":
                // [step1] 获取榜单标题元素
                WebElement rankTitle = driver.findElement(By.xpath("//span[contains(@class, 'rankListTitle')]"));
                // [step2] 提取榜单名称
                rankName = rankTitle.getText();
                System.out.println("[step2] 榜单名称获取成功: " + rankName);

                // [step3] 获取当前窗口句柄（用于后续切换标签页）
                String mainWindow = driver.getWindowHandle();

                // [step4] 点击页面中一个需要提前触发的图片按钮 arkACtExprareImg
                WebElement arkACtExprareImg = driver.findElement(By.xpath("//img[contains(@class,'arkACtExprareImg')]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", arkACtExprareImg);
                System.out.println("[step4] 已点击 arkACtExprareImg 元素");
                try { Thread.sleep((long) (1000)); } catch (InterruptedException ignored) {}

                // [step5] 获取所有商品块（div 元素，容器为 rankContentWrap）
                List<WebElement> productElements = driver.findElements(By.xpath("//div[contains(@class,'rankContentWrap')]/div"));
                System.out.println("[step5] 商品块数量: " + productElements.size());

                // [step6] 遍历每个商品块
                for (int i = 0; i < productElements.size()-1; i++) {
                    try {
                        System.out.println("[step6-" + i + "] 开始处理第 " + i + " 个商品");

                        // [step6.1] 为防止 stale 元素，每次重新获取商品元素列表
                        productElements = driver.findElements(By.xpath("//div[contains(@class,'rankContentWrap')]/div"));
                        WebElement product = productElements.get(i);

                        // [step6.2] 提取商品的排名（第2个 span）
                        WebElement rankElement = product.findElement(By.xpath(".//div[contains(@class,'cardRankWrap')]//span[2]"));
                        rank = rankElement.getText();
                        System.out.println("[step6-" + i + "] 商品排名提取成功: " + rank);

                        // [step6.3] 从商品内部找到链接并在新标签页打开
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", product);
                        System.out.println("[step6-" + i + "] 已打开商品新标签页");

                        // [step6.4] 等待标签页增多
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                        wait.until(d -> d.getWindowHandles().size() > 1);
                        ChromeDriver.waitForPageLoad(driver, DB);
                        driverBlocker(driver);
                        try { Thread.sleep(2000); } catch (Exception _) {}

                        // [step6.5] 切换到新打开的标签页
                        Set<String> allHandles = driver.getWindowHandles();
                        for (String handle : allHandles) {
                            if (!handle.equals(mainWindow)) {
                                driver.switchTo().window(handle);
                                break;
                            }
                        }
                        System.out.println("[step6-" + i + "] 已切换至商品标签页");

                        // [step6.6] 等待新页面加载完成
                        ChromeDriver.waitForPageLoad(driver, DB);
                        driverBlocker(driver);
                        try { Thread.sleep(2000); } catch (Exception _) {}

                        // [step6.7] 获取当前页面的 URL 并解析商品 ID
                        String productUrl = driver.getCurrentUrl();
                        System.out.println("[step6-" + i + "] 商品URL: " + productUrl);
                        try {
                            patternForm = Pattern.compile("[?&]id=(\\d+)");
                            matcher = patternForm.matcher(productUrl);
                            if (matcher.find()) {
                                id = matcher.group(1);
                            } else {
                                id = "N/A";
                            }
                        } catch (Exception e) {
                            id = "N/A";
                        }
                        System.out.println("[step6-" + i + "] 商品ID提取结果: " + id);

                        // [step6.8] 写入数据库
                        insertTaskRanking(DB, new RankingProduct(rankName, id, rank, pageType));
                        System.out.println("[step6-" + i + "] 数据已写入数据库");

                        // [step6.9] 关闭商品标签页
                        driver.close();
                        System.out.println("[step6-" + i + "] 已关闭商品标签页");

                        // [step6.10] 切回原窗口
                        driver.switchTo().window(mainWindow);
                        System.out.println("[step6-" + i + "] 切回原窗口成功");

                    } catch (Exception e) {
                        System.out.println("❌ 第 " + i + " 个商品处理失败：" + e.getMessage());
                        driver.switchTo().window(mainWindow); // 遇异常也回原页面
                    }
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
