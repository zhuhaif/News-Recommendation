package com.five.springboot.bean;

public class Source {
    private String id;
    private String keyword;
    private String source;
    private long sorce;

    public Source( String keyword, String source, long sorce) {
        this.keyword = keyword;
        this.source = source;
        this.sorce = sorce;
    }
    public Source() {

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getSorce() {
        return sorce;
    }

    public void setSorce(long sorce) {
        this.sorce = sorce;
    }
}
