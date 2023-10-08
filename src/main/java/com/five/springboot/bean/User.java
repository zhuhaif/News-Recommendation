package com.five.springboot.bean;

public class User {

    private String userId;
    private String username;
    private String password;
    private String loginTime;

    public User(String userId, String username, String password, String loginTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.loginTime = loginTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }
}
