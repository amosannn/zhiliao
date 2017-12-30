package com.amosannn.model;

import java.util.Date;

public class User {

  private int id;
  private String name;
  private String shortDesc;
  private int gender;
  private String position;
  private String industry;
  private String career;
  private String education;
  private String profile;
  private Date joinDate;
  private String avatarUrl;
  private int viewedCount;
  private int followCount;
  private int followedCount;
  private int collectCount;
  private int likedCount;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShortDesc() {
    return shortDesc;
  }

  public void setShortDesc(String shortDesc) {
    this.shortDesc = shortDesc;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getIndustry() {
    return industry;
  }

  public void setIndustry(String industry) {
    this.industry = industry;
  }

  public String getCareer() {
    return career;
  }

  public void setCareer(String career) {
    this.career = career;
  }

  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public Date getJoinDate() {
    return joinDate;
  }

  public void setJoinDate(Date joinDate) {
    this.joinDate = joinDate;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getViewedCount() {
    return viewedCount;
  }

  public void setViewedCount(int viewedCount) {
    this.viewedCount = viewedCount;
  }

  public int getFollowCount() {
    return followCount;
  }

  public void setFollowCount(int followCount) {
    this.followCount = followCount;
  }

  public int getFollowedCount() {
    return followedCount;
  }

  public void setFollowedCount(int followedCount) {
    this.followedCount = followedCount;
  }

  public int getCollectCount() {
    return collectCount;
  }

  public void setCollectCount(int collectCount) {
    this.collectCount = collectCount;
  }

  public int getLikedCount() {
    return likedCount;
  }

  public void setLikedCount(int likedCount) {
    this.likedCount = likedCount;
  }
}
