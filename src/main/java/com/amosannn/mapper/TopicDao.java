package com.amosannn.mapper;

import com.amosannn.model.Topic;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TopicDao {

  // 话题数量
  int topicCount();

  // 已关注话题数
  int followedTopicCount();

  // 话题广场基础话题
  List<Topic> listBaseTopic();

  // 话题广场子话题（分页
  Map<String, String> listTopic();

  // 话题详情
  Topic topicDetail(@Param("topicId") Integer topicId);

  // 父话题
  List<Topic> listTopicByParentId(@Param("topicId") Integer topicId);
}
