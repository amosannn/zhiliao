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
  private TopicDao topicDao;

  @Override
  public int topicCount() {
    return topicDao.topicCount();
  }

  @Override
  public Map<String, List<Topic>> listAllTopic() {
    Map<String, List<Topic>> map = new HashMap<>();
    List<Topic> baseTopicList = topicDao.listBaseTopic();
    List<Topic> hotTopicList = topicDao.listHotTopic();
    map.put("baseTopicList", baseTopicList);
    map.put("hotTopicList", hotTopicList);
    return map;
  }

  @Override
  public Map<String, Topic> topicDetail(Integer topicId) {
    Topic topicDetail = topicDao.topicDetail(topicId);
    Map<String, Topic> map = new HashMap<>();
    map.put("topicDetail", topicDetail);
    return map;
  }

  @Override
  public Map<String, List<Topic>> listTopicByParentId(Integer topicId) {
    List<Topic> topics = topicDao.listTopicByParentId(topicId);
    Map<String, List<Topic>> map = new HashMap<>();
    map.put("topicId", topics);
    return map;
  }

  /**
   * 获取正在关注的话题列表
   * @param userId
   * @return
   */
  @Override
  public List<Topic> listFollowingTopic(Integer userId) {
    Jedis jedis = jedisPool.getResource();
    Set<String> idSet = jedis.zrange(userId + RedisKey.FOLLOW_TOPIC, 0, -1);
    List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
    List<Topic> topics = topicDao.listFollowingTopic(idList);
    return topics;
  }

  // 关注话题
  @Override
  public boolean followTopic(Integer userId, Integer topicId) {
    boolean status = false;
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(userId + RedisKey.FOLLOW_TOPIC, System.currentTimeMillis(), String.valueOf(topicId));
      jedis.zadd(topicId + RedisKey.FOLLOWED_TOPIC, System.currentTimeMillis(), String.valueOf(userId));
      // 话题被关注数 + 1
      topicDao.updateFollowedCount(topicId, "+ 1");
      status = true;
    }
    return status;
    // 将话题被关注数量加1
  }

  // 取消关注话题
  @Override
  public boolean unfollowTopic(Integer userId, Integer topicId) {
    boolean status = false;
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zrem(userId + RedisKey.FOLLOW_TOPIC, String.valueOf(topicId));
      jedis.zrem(topicId + RedisKey.FOLLOWED_TOPIC, String.valueOf(userId));
      // 话题被关注数 - 1
      topicDao.updateFollowedCount(topicId, "- 1");
      status = true;
    }
    return status;
  }
}
