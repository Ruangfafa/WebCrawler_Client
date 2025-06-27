package org.ruangfafa.Model;

public class Seller {
    private String identifier, name, location, pageType, subscribe, qualityScore, garenteeScore, logisticsScore;

    public Seller(String identifier, String name, String location, String pageType,
                  String subscribe, String qualityScore, String securityScore, String logisticsScore) {
        this.identifier = identifier;
        this.name = name;
        this.location = location;
        this.pageType = pageType;
        this.subscribe = subscribe;
        this.qualityScore = qualityScore;
        this.garenteeScore = securityScore;
        this.logisticsScore = logisticsScore;
    }

    public Seller() {
        this("","","","","","","","");
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(String qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String setGarenteeScore() {
        return garenteeScore;
    }

    public void setGarenteeScore(String garenteeScore) {
        this.garenteeScore = garenteeScore;
    }

    public String getLogisticsScore() {
        return logisticsScore;
    }

    public void setLogisticsScore(String logisticsScore) {
        this.logisticsScore = logisticsScore;
    }
}
