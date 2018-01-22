package com.amosannn.service.impl;

import com.amosannn.mapper.TopicDao;
import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class TopicServiceImpl implements TopicService {

  @Autowired
  private JedisPool jedisPool;

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

  @Override
  public Map<String, List<Topic>> listFollowingTopic(Integer userId) {
    Jedis jedis = jedisPool.getResource();
    Set<String> idSet = jedis.zrange(userId + RedisKey.FOLLOW_TOPIC, 0, -1);
    List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
    List<Topic> topics = dao.listFollowingTopic(idList);
    Map<String, List<Topic>> map = new HashMap<>();
    map.put("followingTopic", topics);
    return map;
  }
}
