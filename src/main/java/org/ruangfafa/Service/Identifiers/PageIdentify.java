package org.ruangfafa.Service.Identifiers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageIdentify {

    public static String searchPageIdentify(WebDriver driver) {
        String url = driver.getCurrentUrl();

        try {
            // 淘宝 C2C 类型 1
            if (url.contains(".taobao.com/search.htm")) {
                try {
                    driver
                    .findElement(By.xpath(".//div[contains(@class, 'search-buttons')]"))
                    .findElement(By.xpath(".//button[contains(@class, 'secondary')]"));
                    return "tb_c2c_1";
                } catch (Exception _) {}
            }

            // 淘宝 C2C ICE 模板
            if (url.contains(".taobao.com/search.htm")) {
                try {
                    driver
                    .findElement(By.xpath(".//div[@id='ice-container']"));
                        return "tb_c2c_ice";
                } catch (Exception _) {}
            }

            // 淘宝 C2C 类型 2
            if (url.contains(".taobao.com/search.htm")) {
                try {
                    driver
                    .findElement(By.xpath(".//div[contains(@class, 'tshop-psm-shop-header2')]"));
                    return "tb_c2c_2";
                } catch (Exception _) {}
            }

            // 天猫国内
            if (url.contains(".tmall.com/search.htm")) {
                return "tm";
            }

            // 天猫国际
            if (url.contains(".tmall.hk/search.htm")) {
                return "tm_global";
            }

            // 京东自营 922474
            if (url.contains("https://mall.jd.com/view_search-")) {
                try {
                    driver
                    .findElement(By.xpath(".//div[contains(@moduletemplateid, '922474')]"));
                    driver
                    .findElement(By.xpath(".//div[contains(@class, 'jLogo')]"))
                    .findElement(By.xpath("./img"));
                    return "jd_zy_922474";
                } catch (Exception _) {}
            }

            // 京东自营 401022
            if (url.contains("https://mall.jd.com/view_search-")) {
                try {
                    driver
                    .findElement(By.xpath(".//div[contains(@moduletemplateid, '401022')]"));
                    driver
                    .findElement(By.xpath(".//div[contains(@class, 'jLogo')]"))
                    .findElement(By.xpath("./img"));
                    return "jd_zy_401022";
                } catch (Exception _) {}
            }

            // 京东非自营 922474
            if (url.contains("https://mall.jd.com/view_search-")) {
                try {
                    driver
                    .findElement(By.xpath(".//div[contains(@moduletemplateid, '922474')]"));
                    driver
                    .findElement(By.xpath(".//div[contains(@class, 'jRating')]"));
                    return "jd_fs_922474";
                } catch (Exception _) {}
            }

        } catch (Exception e) {
            System.out.println("❌ 页面识别失败: " + e.getMessage());
        }

        // 未识别出页面类型
        return "";
    }

    public static String classificatePageIdentify(WebDriver driver) {
        String url = driver.getCurrentUrl();
        try {
            if (url.contains(".tmall.com")) {
                return "tm";
            }
        }
        catch (Exception _) {}
        return "Error";
    }
}