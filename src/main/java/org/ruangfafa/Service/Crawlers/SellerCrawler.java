package org.ruangfafa.Service.Crawlers;

import org.openqa.selenium.WebDriver;
import org.ruangfafa.Model.Seller;

public class SellerCrawler {
    public static Seller craw(WebDriver driver, String url){
        String identifier = "", name = "", location = "", pageType = "", subscribe = "", qualityScore = "", securityScore = "", logisticsScore = "";



        return new Seller(identifier, name, location, pageType, subscribe, qualityScore, securityScore, logisticsScore);
    }
}
