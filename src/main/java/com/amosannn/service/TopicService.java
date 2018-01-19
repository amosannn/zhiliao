package com.amosannn.service;

import com.amosannn.model.Topic;

import java.util.List;
import java.util.Map;

public interface TopicService {

  int topicCount();

  List<Topic> listBaseTopic();

  Map<String, Topic> topicDetail(Integer topicId);

  Map<String, List<Topic>> listTopicByParentId(Integer topicId);

  Map<String, List<Topic>> listFollowingTopic(Integer userId);
}
