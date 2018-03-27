package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.mapper.MessageDao;
import com.amosannn.mapper.QuestionDao;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.Answer;
import com.amosannn.model.AnswerComment;
import com.amosannn.model.Message;
import com.amosannn.model.Question;
import com.amosannn.service.AnswerService;
import com.amosannn.service.CommentService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class AnswerServiceImpl implements AnswerService{

  @Autowired
  private CommentService commentService;
  @Autowired
  AnswerDao answerDao;
  @Autowired
  UserDao userDao;
  @Autowired
  QuestionDao questionDao;
  @Autowired
  MessageDao messageDao;
  @Autowired
  private JedisPool jedisPool;

  @Override
  public Integer answer(Answer answer, Integer userId) {
    answer.setUserId(userId);
    answer.setCreateTime(System.currentTimeMillis());
    Integer answerId = answerDao.insertAnswer(answer);

    Message message = new Message();
    message.setType(Message.TYPE_ANSWER);
    message.setSecondType(1);
    message.setMessageDate(MyUtil.formatDate(new Date()));
    message.setMessageTime(System.currentTimeMillis());
    message.setUserId(userId);
    message.setFromUserName(userDao.selectUsernameByUserId(userId));
    Question question = questionDao.selectQuestionByAnswerId(answer.getAnswerId());
    message.setQuestionId(question.getQuestionId());
    message.setQuestionTitle(question.getQuestionTitle());
    message.setAnswerId(answer.getAnswerId());
    message.setUserId(question.getUserId());
    messageDao.insertTypeComment(message);

    return answerId;
  }

  /**
   * 某用户的回答列表（分页）
   * @param userId
   * @param curPage
   * @return
   */
  @Override
  public List<Answer> listAnswerByUserId(Integer userId, Integer curPage) {
    // 请求页默认为0
    curPage = curPage == null ? 1 : curPage;
    // 每页回答数
    int limit = 8;
    // 问题数坐标
    int offset = (curPage - 1) * limit;

    // 用户回答总数
    int allCount = answerDao.selectAnswerCountByUserId(userId);
    // 总页数
    int allPage = 0;
    if (allCount <= limit) {
      allPage = 1;
    } else if (allCount / limit == 0) {
      allPage = allCount / limit;
    } else {
      allPage = allCount / limit + 1;
    }

    // 构造查询map
    Map<String, Object> map = new HashMap<>();
    map.put("offset", offset);
    map.put("limit", limit);
    map.put("userId", userId);

    // 得到某页回答列表
    List<Answer> answerList = answerDao.listAnswerByUserId(map);

    // 获取各个回答的被点赞数
    try (Jedis jedis = jedisPool.getResource()) {
      for (Answer answer : answerList) {
        Long likedCount = jedis.zcard(answer.getAnswerId() + RedisKey.LIKED_ANSWER);
        answer.setLikedCount(Integer.parseInt(likedCount + ""));
      }
    }

    return answerList;
  }

  @Override
  public Map<String, Object> getAnswerDetail(Integer answerId, Integer userId){
    Map<String, Object> map = new HashMap<>();
    Answer answer = answerDao.selectAnswerByAnswerId(answerId);
    if (answer == null) {
      throw new RuntimeException("该回答不存在");
    }
    try (Jedis jedis = jedisPool.getResource()) {
      // 获取回答点赞数
      Integer likedcount = Integer.parseInt(jedis.zcard(answerId + RedisKey.LIKED_ANSWER)+"");
      answer.setLikedCount(likedcount);
    }

    // 获取回答下评论数
    Integer commentCount = commentService.getAnswerCommentCount(answerId);
    answer.setCommentCount(commentCount);

    // todo 是否收藏过该问题

    // 获取回答下的评论
    List<AnswerComment> answerCommentList = commentService.listAnswerComment(answerId);
    answer.setAnswerCommentList(answerCommentList);

    // 是否点赞过该问题
    Boolean likeState = judgePeopleLikedAnswer(userId, answerId);
    answer.setLikeState(likeState);

    map.put("answer", answer);
    return map;
  }

  /**
   * 判断是否点赞过该问题
   * @param localUserId
   * @param answerId
   * @return
   */
  @Override
  public boolean judgePeopleLikedAnswer(Integer localUserId, Integer answerId) {
    Long rank = null;
    try (Jedis jedis = jedisPool.getResource()){
      rank = jedis.zrank(localUserId + RedisKey.LIKED_ANSWER, String.valueOf(answerId));
    }

    return rank == null ? false:true;
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

  @Override
  public Map<String, Object> listTodayHotAnswer() {
    Map<String, Object> map = new HashMap<>();
    long period = 1000 * 60 * 60 * 24L;
    long today = System.currentTimeMillis();
    System.out.println("period:" + period);
    System.out.println("today:" + today);
    System.out.println("today - period:" + (today - period));
    System.out.println(new Date(today - period));
    List<Answer> answerList = answerDao.listAnswerByCreateTime(today - period);
    map.put("answerList", answerList);
    return map;
  }

  @Override
  public Map<String, Object> listMonthHotAnswer() {
    Map<String, Object> map = new HashMap<>();
    long period = 1000 * 60 * 60 * 24 * 30L;
    long today = System.currentTimeMillis();
    System.out.println("period:" + period);
    System.out.println("today:" + today);
    System.out.println("today - period:" + (today - period));
    System.out.println(new Date(today - period));
    List<Answer> answerList = answerDao.listAnswerByCreateTime(today - period);
    map.put("answerList", answerList);
    return map;
  }
}
