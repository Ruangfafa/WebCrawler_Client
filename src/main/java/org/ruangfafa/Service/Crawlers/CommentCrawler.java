package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ruangfafa.Model.Comment;
import org.ruangfafa.Service.ChromeDriver;
import org.ruangfafa.Service.Identifiers.PageIdentify;

import java.sql.Connection;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ruangfafa.Service.AbnormalProcessing.solveSliderCaptcha;
import static org.ruangfafa.Service.DatabaseService.insertComment;

public class CommentCrawler {

    public static void craw(WebDriver driver, String url, Connection DB) {
        System.out.println("[step0] 开始 CommentCrawler.craw");
        String pageType = "", id = "", comment = "", pattern = "";
        Date date;

        driver.get(url);
        System.out.println("[step1] driver.get 完成");

        ChromeDriver.waitForPageLoad(driver, DB);
        System.out.println("[step2] 页面加载完成");

        try { Thread.sleep((long) (Math.random() * 5000)); } catch (InterruptedException ignored) {}

        solveSliderCaptcha(driver);
        System.out.println("[step4] 滑块验证结束");

        pageType = PageIdentify.classificatePageIdentify(driver);
        System.out.println("[step5] 页面类型识别结果: " + pageType);

        // 初始化变量
        int loadedCommentsCount = 0; // 记录已加载评论数
        int prevLoadedCount = 0; // 上一轮已加载评论数
        boolean keepScrolling = true; // 控制是否继续滚动

        switch (pageType) {
            case "tb_c2c_1":
            case "tb_c2c_2":
            case "tb_c2c_ice":
            case "tb_global":
            case "jd_zy_922474":
            case "jd_zy_401022":
            case "jd_fs_922474":
                System.out.println("[step6] 当前页面暂不处理: " + pageType);
                break;

            case "tm":
                try {
                    System.out.println("[step7] 进入 TM 页面逻辑");

                    // 提取商品 ID
                    id = extractItemIdFromUrl(url);
                    System.out.println("[step8] 商品 ID 提取完毕: " + id);

                    // 点击 ShowButton 按钮
                    WebElement showButton = driver.findElement(By.xpath("//div[contains(@class,'ShowButton')]"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", showButton);
                    System.out.println("[step9] 已点击 ShowButton");

                    try { Thread.sleep((long) (Math.random() * 5000)); } catch (InterruptedException ignored) {}

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

                    // 按时间排序: 先点击箭头，再点击"时间排序"
                    WebElement arrowBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class,'leftDrawer')]//i[contains(@class, 'arrowIcon')]")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", arrowBtn);
                    System.out.println("[step10] 成功点击评论排序箭头");

                    // 点击时间排序按钮
                    WebElement sortButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class,'leftDrawer')]//div[contains(@class, 'sortItem') and contains(text(), '时间排序')]")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sortButton);
                    System.out.println("[step11] 已点击时间排序按钮");

                    // 等待评论容器加载并找到评论区域
                    WebElement commentContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class,'leftDrawer')]//div[contains(@class,'comments')]")));
                    System.out.println("[step12] 成功找到评论容器");

                    Actions actions = new Actions(driver);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
                    LocalDate today = LocalDate.now(); // 获取当前日期

                    // 设置n天有效期过滤
                    long validDays = 365; // 设置有效期为180天

                    int currentScrollAttempts = 0;
                    // 开始滚动评论区域并抓取评论
                    crawC: while (keepScrolling) {
                        // 获取当前页面的评论
                        List<WebElement> comments = commentContainer.findElements(By.xpath(".//div[contains(@class,'Comment')]"));
                        System.out.println("[step13] 当前页面评论数：" + comments.size());

                        // 如果评论数量少于20条，且已经加载完所有评论，则结束抓取

                        if (comments.size() <= prevLoadedCount) {
                            currentScrollAttempts++; // 增加滑动尝试次数
                            System.out.println("[step16] 无新评论，滑动尝试次数: " + currentScrollAttempts);

                            // 如果滑动尝试次数已达到最大值，则停止滑动
                            if (currentScrollAttempts >= 3) {
                                System.out.println("[step16] 达到最大滑动尝试次数，终止滚动");
                                keepScrolling = false;
                                break; // 退出循环，停止滚动
                            }
                        } else {
                            // 如果找到了新评论，则重置滑动尝试次数
                            currentScrollAttempts = 0;
                        }

                        // 只处理新加载的评论
                        for (int i = prevLoadedCount; i < comments.size(); i++) {
                            WebElement commentElem = comments.get(i);
                            try {
                                // 提取评论的日期和内容
                                WebElement metaElem = commentElem.findElement(By.xpath(".//div[contains(@class,'meta')]"));
                                String[] metaParts = metaElem.getText().split("·");
                                String dateText = metaParts[0].trim();
                                String patternText = (metaParts.length > 1) ? metaParts[1].trim() : "<None>";
                                LocalDate commentDate = LocalDate.parse(dateText, formatter);

                                // 判断评论是否在有效期内（过去180天）
                                if (commentDate.isBefore(today.minusDays(validDays))) {
                                    break crawC; // 如果评论不在有效期内，跳过该评论
                                }

                                // 提取评论的内容并插入数据库
                                WebElement contentElem = commentElem.findElement(By.xpath(".//div[contains(@class,'content')]"));
                                comment = contentElem.getText().trim();
                                pattern = patternText;
                                date = Date.valueOf(commentDate);

                                insertComment(DB, new Comment(pageType, id, comment, pattern, date));
                                System.out.println("[step15] 新评论插入成功: " + date + " | " + pattern + " | " + comment);

                                try { Thread.sleep((long) (Math.random() * 200)); } catch (InterruptedException ignored) {}

                            } catch (Exception innerEx) {
                                System.out.println("[WARN] 单条评论处理失败：" + innerEx.getMessage());
                            }
                        }

                        prevLoadedCount = comments.size(); // 更新已加载的评论数量
                        // 滚动评论容器
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", commentContainer);
                        try {
                            Thread.sleep((long) (Math.random() * 5000)); // 延迟5秒后继续
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("[step17] 滑动并进入下一轮，累计评论数: " + prevLoadedCount);
                    }

                    System.out.println("[step18] 所有评论抓取完毕");
                } catch (Exception e) {
                    System.out.println("[ERROR] TM 页面评论处理异常：" + e.getMessage());
                    e.printStackTrace();
                }
                break;
        }

        System.out.println("[step99] CommentCrawler.craw 执行完毕");
    }

    // 提取商品ID的辅助函数
    private static String extractItemIdFromUrl(String url) {
        Pattern patternForm = Pattern.compile("[?&]id=(\\d+)");
        Matcher matcher = patternForm.matcher(url);
        return matcher.find() ? matcher.group(1) : "N/A";
    }
}
