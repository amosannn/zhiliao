package com.amosannn.model;

public class Collection {

  private int id;
  private String name;
  private String createTime;
  private String updateTime;
  private int followedCount;
  private int userId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }

  public int getFollowedCount() {
    return followedCount;
  }

  public void setFollowedCount(int followedCount) {
    this.followedCount = followedCount;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}
