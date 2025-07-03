package org.ruangfafa.Model;

import java.sql.Date;

public class Comment {
    private String pageType, id, comment, pattern;
    private Date date;

    public Comment(String pageType, String id, String comment, String pattern, Date date) {
        this.pageType = pageType;
        this.id = id;
        this.comment = comment;
        this.pattern = pattern;
        this.date = date;
    }

    public String getPageType() {
        return pageType;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getPattern() {
        return pattern;
    }

    public Date getDate() {
        return date;
    }
}
