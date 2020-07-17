package com.hackerNews.rediscachedemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties
public class Comment implements Serializable {
    private String text;
    private String user;
    private String userHandleAge;
    private Integer commentCount;

    public Comment(){}

    public Comment(String text, String by){
        this.text = text;
        this.user = by;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserHandleAge() {
        return userHandleAge;
    }

    public void setUserHandleAge(String userHandleAge) {
        this.userHandleAge = userHandleAge;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
