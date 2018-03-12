package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.mapper.CollectionDao;
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
  JedisPool jedisPool;

  /**
   * 列出自己创建的收藏夹
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

}
