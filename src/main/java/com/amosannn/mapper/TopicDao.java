package com.amosannn.mapper;

import com.amosannn.model.Topic;

import java.util.List;
import java.util.Map;

public interface TopicDao {
    // 话题数量
    int topicCount();

    // 已关注话题数
    int followedTopicCount();

    // 话题广场基础话题
    List<Topic> listBaseTopic();

    // 话题广场子话题（分页
    Map<String, String> listTopic();
}
