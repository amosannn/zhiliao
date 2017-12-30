package com.amosannn.model;

import java.util.Date;

public class Question {

  private int id;
  private String title;
  private String content;
  private int topicId;
  private int followedCount;
  private int viewedCount;
  private Date createTime;
  private int userId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getTopicId() {
    return topicId;
  }

  public void setTopicId(int topicId) {
    this.topicId = topicId;
  }

  public int getFollowedCount() {
    return followedCount;
  }

  public void setFollowedCount(int followedCount) {
    this.followedCount = followedCount;
  }

  public int getViewedCount() {
    return viewedCount;
  }

  public void setViewedCount(int viewedCount) {
    this.viewedCount = viewedCount;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}
