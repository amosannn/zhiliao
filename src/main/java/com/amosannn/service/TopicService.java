package com.amosannn.service;

import com.amosannn.model.Topic;

import java.util.List;

public interface TopicService {
    int topicCount();

    List<Topic> listBaseTopic();
}
