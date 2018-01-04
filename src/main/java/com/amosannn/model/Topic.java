package com.amosannn.model;

public class Topic {

  private int topicId;
  private String topicName;
  private String topicDesc;
  private String topicImage;
  private int parentTopicId;
  private int followedCount;

  public int getTopicId() {
    return topicId;
  }

  public void setTopicId(int topicId) {
    this.topicId = topicId;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public String getTopicDesc() {
    return topicDesc;
  }

  public void setTopicDesc(String topicDesc) {
    this.topicDesc = topicDesc;
  }

  public String getTopicImage() {
    return topicImage;
  }

  public void setTopicImage(String topicImage) {
    this.topicImage = topicImage;
  }

  public int getParentTopicId() {
    return parentTopicId;
  }

  public void setParentTopicId(int parentTopicId) {
    this.parentTopicId = parentTopicId;
  }

  public int getFollowedCount() {
    return followedCount;
  }

  public void setFollowedCount(int followedCount) {
    this.followedCount = followedCount;
  }
}
