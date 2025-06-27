package org.ruangfafa.Model;

public class Product {
    private String pageType, id, skuid, orgnPrice, diskPrice, title, soldAmount365,
            storageAddress, guarantee, pattern, storageLeft, parameter,
            patternCom, image, imageSet;

    public Product(String pageType, String id, String skuid, String orgnPrice, String diskPrice,
                   String title, String soldAmount365, String storageAddress, String guarantee,
                   String pattern, String storageLeft, String parameter,
                   String patternCom, String image, String imageSet) {
        this.pageType = pageType;
        this.id = id;
        this.skuid = skuid;
        this.orgnPrice = orgnPrice;
        this.diskPrice = diskPrice;
        this.title = title;
        this.soldAmount365 = soldAmount365;
        this.storageAddress = storageAddress;
        this.guarantee = guarantee;
        this.pattern = pattern;
        this.storageLeft = storageLeft;
        this.parameter = parameter;
        this.patternCom = patternCom;
        this.image = image;
        this.imageSet = imageSet;
    }

    public Product() {
        this("", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid;
    }

    public String getOrgnPrice() {
        return orgnPrice;
    }

    public void setOrgnPrice(String orgnPrice) {
        this.orgnPrice = orgnPrice;
    }

    public String getDiskPrice() {
        return diskPrice;
    }

    public void setDiskPrice(String diskPrice) {
        this.diskPrice = diskPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSoldAmount365() {
        return soldAmount365;
    }

    public void setSoldAmount365(String soldAmount365) {
        this.soldAmount365 = soldAmount365;
    }

    public String getStorageAddress() {
        return storageAddress;
    }

    public void setStorageAddress(String storageAddress) {
        this.storageAddress = storageAddress;
    }

    public String getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(String guarantee) {
        this.guarantee = guarantee;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getStorageLeft() {
        return storageLeft;
    }

    public void setStorageLeft(String storageLeft) {
        this.storageLeft = storageLeft;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getPatternCom() {
        return patternCom;
    }

    public void setPatternCom(String patternCom) {
        this.patternCom = patternCom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageSet() {
        return imageSet;
    }

    public void setImageSet(String imageSet) {
        this.imageSet = imageSet;
    }
}