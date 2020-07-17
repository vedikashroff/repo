package com.hackerNews.rediscachedemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.StringUtils;

import java.io.Serializable;


@JsonIgnoreProperties
public class Story implements Serializable
{
    private int storyId;
    private String title;
    private String url;
    private int score;
    private long timeOfSubmission;
    private String userName;

    public Story(){}

    public Story(Items items){
        if(!StringUtils.isEmpty(items.getId())){
            this.storyId = items.getId();
        }
        if(!StringUtils.isEmpty(items.getTitle())){
            this.title = items.getTitle();
        }
        if(!StringUtils.isEmpty(items.getUrl())){
            this.url = items.getUrl();
        }
        this.score = items.getScore();
        this.timeOfSubmission = items.getTime();
        this.userName = items.getBy();
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTimeOfSubmission() {
        return timeOfSubmission;
    }

    public void setTimeOfSubmission(long timeOfSubmission) {
        this.timeOfSubmission = timeOfSubmission;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
