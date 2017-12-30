package com.amosannn.model;

public class Answer {

  private int id;
  private String content;
  private int topicId;
  private int questionId;
  private int likeCount;
  private int userId;
  private int createTime;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public int getQuestionId() {
    return questionId;
  }

  public void setQuestionId(int questionId) {
    this.questionId = questionId;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getCreateTime() {
    return createTime;
  }

  public void setCreateTime(int createTime) {
    this.createTime = createTime;
  }
}
