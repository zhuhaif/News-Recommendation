package com.five.springboot.bean;

public class GoodSource {
    private String id;
    private String keyword;
    private String sources;

    public GoodSource(String keyword, String sources) {
        this.keyword = keyword;
        this.sources = sources;
    }
    public GoodSource() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }
}
