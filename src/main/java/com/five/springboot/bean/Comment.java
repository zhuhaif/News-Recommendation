package com.five.springboot.bean;

public class Comment {

    private String commentId;
    private String newsId;
    private String username;
    private String content;
    private String commentTime;

    public Comment(String commentId, String newsId, String username, String content, String commentTime) {
        this.commentId = commentId;
        this.newsId = newsId;
        this.username = username;
        this.content = content;
        this.commentTime = commentTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
