package com.amosannn.service;

import com.amosannn.model.Topic;

import java.util.List;
import java.util.Map;

public interface TopicService {

  int topicCount();

  Map<String, List<Topic>> listAllTopic();

  Map<String, Object> topicDetail(Integer topicId, Integer curPage, String tabType, Integer userId);

  Map<String, List<Topic>> listParentTopic(Integer userId);

  Map<String, List<Topic>> listTopicByParentId(Integer topicId);

  List<Topic> listFollowingTopic(Integer userId);

  boolean followTopic(Integer userId, Integer topicId);

  boolean unfollowTopic(Integer userId, Integer topicId);

  boolean judgePeopleFollowTopic(Integer userId, Integer topicId);

  List<Topic> listTopicByTopicName(String topicName);
}
