package org.ruangfafa.Model;

public class Classificate {
    private String pageType, identifier, category_pv, cName;

    public Classificate(String pageType, String identifier, String category_pv, String cName) {
        this.pageType = pageType;
        this.identifier = identifier;
        this.category_pv = category_pv;
        this.cName = cName;
    }

    public Classificate() {
        this("", "", "", "");
    }

    public String getPageType() { return pageType; }
    public void setPageType(String pageType) { this.pageType = pageType; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getCategory_pv() { return category_pv; }
    public void setCategory_pv(String category_pv) { this.category_pv = category_pv; }

    public String getCName() { return cName; }
    public void setCName(String cName) { this.cName = cName; }
}