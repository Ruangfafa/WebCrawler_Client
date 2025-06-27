package org.ruangfafa.Model;

public class ProductTag {
    private String pageType;
    private String identifier;
    private String id;
    private String soldAmount;
    private String tag;

    public ProductTag(String pageType, String identifier, String id, String soldAmount, String tag) {
        this.pageType = pageType;
        this.identifier = identifier;
        this.id = id;
        this.soldAmount = soldAmount;
        this.tag = tag;
    }

    public ProductTag() {
        this("", "", "", "", "");
    }

    public String getPageType() { return pageType; }
    public void setPageType(String pageType) { this.pageType = pageType; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSoldAmount() { return soldAmount; }
    public void setSoldAmount(String soldAmount) { this.soldAmount = soldAmount; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}