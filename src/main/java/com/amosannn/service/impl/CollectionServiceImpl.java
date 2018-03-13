package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.mapper.CollectionDao;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.Answer;
import com.amosannn.model.Collection;
import com.amosannn.service.CollectionService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class CollectionServiceImpl implements CollectionService {

  @Autowired
  CollectionDao collectionDao;
  @Autowired
  AnswerDao answerDao;
  @Autowired
  UserDao userDao;

  @Autowired
  JedisPool jedisPool;

  // 创建收藏夹
  @Override
  public Integer addCollection(Collection collection, Integer userId) {
    collection.setUserId(userId);
    collection.setCreateTime(System.currentTimeMillis());
    collection.setUpdateTime(System.currentTimeMillis());
    Integer collectionId = collectionDao.insertCollection(collection);
    return collectionId;
  }

  /**
   * 列出自己创建的收藏夹列表
   * @param userId
   * @return
   */
  @Override
  public List<Collection> listCreatingCollection(Integer userId) {
    List<Collection> collectionList = new ArrayList<>();
    // 获取收藏夹列表
    collectionList = collectionDao.listCreatingCollectionByUserId(userId);
    // 获取每个收藏夹内的答案条数
    try (Jedis jedis = jedisPool.getResource()) {
      for (Collection collection : collectionList) {
        Long answerCount = jedis.zcard(collection.getCollectionId() + RedisKey.COLLECT);
        collection.setAnswerCount(Integer.parseInt(answerCount+""));
      }
    }

    return collectionList;
  }

  /**
   * 获取收藏夹内容（收藏夹详情、回答）
   * @param collectionId
   * @param localUserId
   * @return
   */
  @Override
  public Map<String, Object> getCollectionContent(Integer collectionId, Integer localUserId) {
    Map<String, Object> map = new HashMap<>();
    try (Jedis jedis = jedisPool.getResource()) {
      // 获取收藏夹内的回答id列表
      Set<String> idSet = jedis.zrange(collectionId + RedisKey.COLLECT, 0, -1);
      List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
      if (idList.size() > 0) {
        // 取出收藏夹内的回答
        List<Answer> answerList = answerDao.listAnswerByAnswerId(idList);
        map.put("answerList", answerList);
      }

      // 判断是不是在获取自己的收藏夹信息
      Integer userId = collectionDao.selectUserIdByCollectionId(collectionId);
      if (userId.equals(localUserId)) {
        map.put("isSelf", true);
      }

      // 获取收藏夹信息
      Collection collection = collectionDao.selectCollectionByCollectionId(collectionId);
      Long answerCount = jedis.zcard(collectionId + RedisKey.COLLECT);
      collection.setAnswerCount(Integer.parseInt(answerCount + ""));
      map.put("collection", collection);
    }

    return map;
  }

  /**
   * 判断收藏夹是否包含某问题
   * @param collectionId
   * @param answerId
   * @return
   */
  @Override
  public Boolean collectionContainAnswer(Integer collectionId, Integer answerId) {
    try (Jedis jedis = jedisPool.getResource()) {
      Long rank = jedis.zrank(collectionId + RedisKey.COLLECT, String.valueOf(answerId));
      if (null == rank) {
        return false;
      }
    }
    return true;
  }

  /**
   * 判断用户是否关注某收藏夹
   * @param userId
   * @param collectionId
   * @return
   */
  @Override
  public Boolean judgePeopleFollowCollection(Integer userId, Integer collectionId) {
    try (Jedis jedis = jedisPool.getResource()) {
      Long rank = jedis.zrank(userId + RedisKey.FOLLOW_COLLECTION, String.valueOf(collectionId));
      if (null == rank) {
        return false;
      }
    }
    return true;
  }

  /**
   * 收藏回答
   * @param collectionId
   * @param answerId
   * @return
   */
  @Override
  public Boolean collectAnswer(Integer collectionId, Integer answerId) {
    Boolean status = false;
    // 更新用户回答被收藏数量
    userDao.updateCollectedCountByAnswerId(answerId, 1);
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(collectionId + RedisKey.COLLECT, System.currentTimeMillis(), String.valueOf(answerId));
      jedis.zadd(answerId + RedisKey.COLLECTED, System.currentTimeMillis(), String.valueOf(collectionId));
      status = true;
    }
    return status;
  }

  /**
   * 取消收藏回答
   * @param collectionId
   * @param answerId
   * @return
   */
  @Override
  public Boolean uncollectAnswer(Integer collectionId, Integer answerId) {
    Boolean status = false;
    // 更新用户回答被收藏数量
    userDao.updateCollectedCountByAnswerId(answerId, -1);
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zrem(collectionId + RedisKey.COLLECT, String.valueOf(answerId));
      jedis.zrem(answerId + RedisKey.COLLECTED, String.valueOf(collectionId));
      status = true;
    }
    return status;
  }

  /**
   * 关注收藏夹
   * @param userId
   * @param collectionId
   * @return
   */
  @Override
  public Boolean followCollection(Integer userId, Integer collectionId) {
    boolean status = false;
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(userId + RedisKey.FOLLOW_COLLECTION, System.currentTimeMillis(), String.valueOf(collectionId));
      jedis.zadd(collectionId + RedisKey.FOLLOWED_COLLECTION, System.currentTimeMillis(), String.valueOf(userId));
      status = true;
    }
    return status;
  }

  /**
   * 取消关注收藏夹
   * @param userId
   * @param collectionId
   * @return
   */
  @Override
  public Boolean unfollowCollection(Integer userId, Integer collectionId) {
    boolean status = false;
    try (Jedis jedis = jedisPool.getResource()) {
    jedis.zrem(userId + RedisKey.FOLLOW_COLLECTION, String.valueOf(collectionId));
    jedis.zrem(collectionId + RedisKey.FOLLOWED_COLLECTION, String.valueOf(userId));
    status = true;
  }
    return status;
  }

}
