package com.amosannn.util;

public class RedisKey {

  // 某收藏夹收藏了那些回答
  public static final String COLLECT = ":collect";
  // 某回答被哪些收藏夹收录
  public static final String COLLECTED = ":collected";

  // 某人关注的人们
  public static final String FOLLOW_USER = ":follow_user";
  // 某人的粉丝们
  public static final String FOLLOWED_USER = ":followed_user";

  // 某人关注的话题们
  public static final String FOLLOW_TOPIC = ":follow_topic";
  // 某话题的关注者们
  public static final String FOLLOWED_TOPIC = ":followed_topic";

  // 某人关注的问题们
  public static final String FOLLOW_QUESTION = ":follow_question";
  // 某问题的关注者们
  public static final String FOLLOWED_QUESTION = ":followed_question";

  // 某人关注的收藏夹
  public static final String FOLLOW_COLLECTION = ":follow_collection";
  // 某收藏夹的关注者们
  public static final String FOLLOWED_COLLECTION = ":followed_collection";

  // 某问题被浏览次数
  public static final String QUESTION_SCANED_COUNT = ":question_scaned_count";

  // 某人点赞的回答们
  public static final String LIKE_ANSWER = ":like_answer";
  // 某回答被哪些人点赞
  public static final String LIKED_ANSWER = ":liked_answer";

}
