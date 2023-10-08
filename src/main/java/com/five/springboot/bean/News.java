package com.five.springboot.bean;


public class News {

    private String newsId;
    private String title;
    private String moduleId;
    private String summary;
    private String date;
    private String source;
    private long heat;
    private long readNum;
    private long commentsNum;
    private long forwardNum;
    private long favorNum;
    private String content;
    private String pictureUrls;
    private String url;
    private String keyword;
    private String[] pictureUrlArray;

    public News(String newsId, String title, String moduleId, String summary, String date, String source, long heat, long readNum, long commentsNum, long forwardNum, long favorNum, String content, String pictureUrls, String url, String keyword) {
        this.newsId = newsId;
        this.title = title;
        this.moduleId = moduleId;
        this.summary = summary;
        this.date = date;
        this.source = source;
        this.heat = heat;
        this.readNum = readNum;
        this.commentsNum = commentsNum;
        this.forwardNum = forwardNum;
        this.favorNum = favorNum;
        this.content = content;
        this.pictureUrls = pictureUrls;
        this.url = url;
        this.keyword = keyword;
        this.pictureUrlArray = null;
    }

    public String[] getPictureUrlArray() {
        return pictureUrlArray;
    }

    public void setPictureUrlArray(String[] pictureUrlArray) {
        this.pictureUrlArray = pictureUrlArray;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getHeat() {
        return heat;
    }

    public void setHeat(long heat) {
        this.heat = heat;
    }

    public long getReadNum() {
        return readNum;
    }

    public void setReadNum(long readNum) {
        this.readNum = readNum;
    }

    public long getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(long commentsNum) {
        this.commentsNum = commentsNum;
    }

    public long getForwardNum() {
        return forwardNum;
    }

    public void setForwardNum(long forwardNum) {
        this.forwardNum = forwardNum;
    }

    public long getFavorNum() {
        return favorNum;
    }

    public void setFavorNum(long favorNum) {
        this.favorNum = favorNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPictureUrls() {
        return pictureUrls;
    }

    public void setPictureUrls(String pictureUrls) {
        this.pictureUrls = pictureUrls;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
