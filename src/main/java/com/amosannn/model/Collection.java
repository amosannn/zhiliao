package com.amosannn.model;

public class Collection {

  private Integer collectionId;
  private String collectionName;
  private String collectionDesc;
  private Long createTime;
  private Long updateTime;

  private Integer userId;

  private Integer followedCount;
  private Integer answerCount;
  private User user;

  public Integer getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(Integer collectionId) {
    this.collectionId = collectionId;
  }

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public String getCollectionDesc() {
    return collectionDesc;
  }

  public void setCollectionDesc(String collectionDesc) {
    this.collectionDesc = collectionDesc;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getFollowedCount() {
    return followedCount;
  }

  public void setFollowedCount(Integer followedCount) {
    this.followedCount = followedCount;
  }

  public Integer getAnswerCount() {
    return answerCount;
  }

  public void setAnswerCount(Integer answerCount) {
    this.answerCount = answerCount;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
