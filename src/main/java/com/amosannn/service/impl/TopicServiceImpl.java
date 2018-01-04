package com.amosannn.service.impl;

import com.amosannn.mapper.TopicDao;
import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

  @Resource
  private TopicDao dao;

  @Override
  public int topicCount() {
    return dao.topicCount();
  }

  @Override
  public List<Topic> listBaseTopic() {
    return dao.listBaseTopic();
  }

  @Override
  public Map<String, Topic> topicDetail(Integer topicId) {
    Topic topicDetail = dao.topicDetail(topicId);
    Map<String, Topic> map = new HashMap<>();
    map.put("topicDetail", topicDetail);
    return map;
  }

  @Override
  public Map<String, List<Topic>> listTopicByParentId(Integer topicId) {
    List<Topic> topics = dao.listTopicByParentId(topicId);
    Map<String, List<Topic>> map = new HashMap<>();
    map.put("topicId", topics);
    return map;
  }

}
