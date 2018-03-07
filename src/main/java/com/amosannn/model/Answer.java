package com.amosannn.model;

import java.util.List;

public class Answer {

  private Integer answerId;
  private String answerContent;
  private Integer likeCount;
  private Long createTime;

  private Integer userId;
  private Integer questionId;
  private String likeState;
  private Integer commentCount;

  private Question question;
  private User user;

  private List<AnswerComment> answerCommentList;

  public Integer getAnswerId() {
    return answerId;
  }

  public void setAnswerId(Integer answerId) {
    this.answerId = answerId;
  }

  public String getAnswerContent() {
    return answerContent;
  }

  public void setAnswerContent(String answerContent) {
    this.answerContent = answerContent;
  }

  public Integer getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Integer questionId) {
    this.questionId = questionId;
  }

  public String getLikeState() {
    return likeState;
  }

  public void setLikeState(String likeState) {
    this.likeState = likeState;
  }

  public Integer getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Integer commentCount) {
    this.commentCount = commentCount;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<AnswerComment> getAnswerCommentList() {
    return answerCommentList;
  }

  public void setAnswerCommentList(List<AnswerComment> answerCommentList) {
    this.answerCommentList = answerCommentList;
  }
}
