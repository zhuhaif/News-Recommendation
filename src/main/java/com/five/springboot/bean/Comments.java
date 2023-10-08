package com.five.springboot.bean;

public class Comments {
     private String newsId;
     private String commentId;
     private String username;
     private String  dateTime;
     private String content;



    public Comments() {
    }

    public Comments(String newsId, String commentId, String username, String dateTime, String content) {
        this.newsId = newsId;
        this.commentId = commentId;
        this.username = username;
        this.dateTime = dateTime;
        this.content = content;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
