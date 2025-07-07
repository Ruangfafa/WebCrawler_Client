package org.ruangfafa.Model;

public class RankingProduct {
    private String rankName, id, rank, pageType;

    public RankingProduct(String rankName, String id, String rank, String pageType) {
        this.rankName = rankName;
        this.id = id;
        this.rank = rank;
        this.pageType = pageType;
    }

    public String getRankName() {
        return rankName;
    }

    public String getId() {
        return id;
    }

    public String getRank() {
        return rank;
    }

    public String getPageType() {
        return pageType;
    }
}
