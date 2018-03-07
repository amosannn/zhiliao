package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.Answer;
import com.amosannn.service.AnswerService;
import com.amosannn.util.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class AnswerServiceImpl implements AnswerService{

  @Autowired
  AnswerDao answerDao;
  @Autowired
  UserDao userDao;
  @Autowired
  private JedisPool jedisPool;

  @Override
  public Integer answer(Answer answer, Integer userId) {
    answer.setUserId(userId);
    answer.setCreateTime(System.currentTimeMillis());
    Integer answerId = answerDao.insertAnswer(answer);

//    answer.setAnswerId(answerId);

    return answerId;
  }

  @Override
  public void likeAnswer(Integer answerId, Integer userId) {
    // 更新答案点赞数
    answerDao.updateLikedCount(answerId, 1);

    // 更新用户被点赞数
    userDao.updateLikedCountByAnswerId(answerId, 1);

    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(userId + RedisKey.LIKE_ANSWER, System.currentTimeMillis(),
          String.valueOf(answerId));
      jedis.zadd(answerId + RedisKey.LIKED_ANSWER, System.currentTimeMillis(),
          String.valueOf(userId));
    }

  }

  @Override
  public void unlikeAnswer(Integer answerId, Integer userId) {
    // 更新答案点赞数
    answerDao.updateLikedCount(answerId, -1);

    // 更新用户被点赞数
    userDao.updateLikedCountByAnswerId(answerId, -1);

    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zrem(userId + RedisKey.LIKE_ANSWER, String.valueOf(answerId));
      jedis.zrem(answerId + RedisKey.LIKED_ANSWER, String.valueOf(userId));
    }

  }

}
