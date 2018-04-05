package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.mapper.CommentDao;
import com.amosannn.mapper.QuestionDao;
import com.amosannn.mapper.TopicDao;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.Answer;
import com.amosannn.model.AnswerComment;
import com.amosannn.model.PageBean;
import com.amosannn.model.Question;
import com.amosannn.model.QuestionComment;
import com.amosannn.model.Topic;
import com.amosannn.model.User;
import com.amosannn.service.QuestionService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import com.amosannn.util.ResponseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties.Redis;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class QuestionServiceImpl implements QuestionService {

  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private TopicDao topicDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private AnswerDao answerDao;

  @Autowired
  private CommentDao commentDao;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JedisPool jedisPool;

  @Override
  public Integer ask(Question question, String topicNames, Integer userId) {
    String[] topicNameArr = topicNames.split(",|，");
    Map<Integer, String> map = new HashMap<>();
    List<Integer> topicIdList = new ArrayList<>();
    for (String topicName : topicNameArr) {
      Topic topic = new Topic();
      Integer topicId = topicDao.selectTopicIdByTopicName(topicName);
      if (topicId == null) {
        topic.setParentTopicId(1);
        topic.setTopicName(topicName);
        topicDao.insertTopic(topic);// TopicDao处理topicId的赋值（注解）
        topicId = topic.getTopicId();
      }
      map.put(topicId, topicName);
      topicIdList.add(topicId);
    }
    String topicKvList = null;
    try {
      topicKvList = objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
    }
    question.setTopicKvList(topicKvList);
    question.setCreateTime(System.currentTimeMillis());
    question.setUserId(userId);
    questionDao.insertQuestion(question);

    // 向关联表插入数据
    for (Integer topicId : topicIdList) {
      questionDao.insertIntoQuestionTopic(question.getQuestionId(), topicId);
    }
    return question.getQuestionId();
  }

  @Override
  public Map<String, Object> getQuestionDetail(Integer questionId, Integer userId) {
    Map<String, Object> map = new HashMap<>();
    // 获取问题信息
    Question question = questionDao.selectQuestionByQuestionId(questionId);
    question.setQuestionId(questionId);
    if (question == null) {
      throw new RuntimeException("该问题不存在");
    }
    List<User> followedUserList = null;
    try (Jedis jedis = jedisPool.getResource()) {
      // 获取该问题被浏览次数
      jedis.zincrby(RedisKey.QUESTION_SCANED_COUNT, 1, questionId + "");
      question.setScanedCount(
          (int) jedis.zscore(RedisKey.QUESTION_SCANED_COUNT, questionId + "").doubleValue());

      // 获取该问题被关注人数
      Long followedCount = jedis.zcard(questionId + RedisKey.FOLLOWED_QUESTION);
      question.setFollowedCount(Integer.parseInt(followedCount + ""));

      // 获取10个关注该问题的人
      Set<String> userIdSet = jedis.zrange(questionId + RedisKey.FOLLOWED_QUESTION, 0, 9);
      List<Integer> userIdList = MyUtil.StringSetToIntegerList(userIdSet);
      followedUserList = new ArrayList<>();
      if (userIdList.size() > 0) {
        followedUserList = userDao.listUserInfoByUserId(userIdList);
      }
    }

    // todo 相似问题（推送5条同话题提问

    // 提问人的用户信息
    User questioner = userDao.selectProfileInfoByUserId(question.getUserId());
    question.setUser(questioner);

    // 问题评论列表（绑定评论者用户信息
    List<QuestionComment> questionCommentList = commentDao.listQuestionCommentByQuestionId(questionId);
    // 为每条问题评论绑定用户信息
    try (Jedis jedis = jedisPool.getResource()) {
      for (QuestionComment comment : questionCommentList) {
        User commentUser = userDao.selectUserInfoByUserId(comment.getUserId());
        comment.setUser(commentUser);
        // 判断用户是否赞过该评论
        Long rank = jedis
            .zrank(userId + RedisKey.LIKE_QUESTION_COMMENT, comment.getQuestionCommentId() + "");
        comment.setLikeState(rank == null ? false : true);
        // 获取该评论被点赞次数
        Long likedCount = jedis
            .zcard(comment.getQuestionCommentId() + RedisKey.LIKED_QUESTION_COMMENT);
        comment.setLikedCount(Integer.valueOf(likedCount + ""));
      }
    }
    question.setQuestionCommentList(questionCommentList);

    // 答案列表（绑定回答者用户信息，点赞情况
    List<Answer> answerList = answerDao.selectAnswerByQuestionId(questionId);
    for (Answer answer : answerList) {
      User answerUser = userDao.selectUserInfoByUserId(answer.getUserId());
      answer.setUser(answerUser);
      // 获取答案评论列表
      List<AnswerComment> answerCommentList = commentDao.listAnswerCommentByAnswerId(answer.getAnswerId());
      try (Jedis jedis = jedisPool.getResource()) {
        for (AnswerComment comment : answerCommentList) {
          // 为评论绑定用户信息
          User commentUser = userDao.selectUserInfoByUserId(comment.getUserId());
          comment.setUser(commentUser);
          // 判断用户是否赞过该评论
          Long rank = jedis.zrank(userId + RedisKey.LIKE_ANSWER_COMMENT, comment.getAnswerCommentId() + "");
          comment.setLikeState(rank == null ? "false" : "true");
          // 获取该评论被点赞次数
          Long likedCount = jedis.zcard(comment.getAnswerCommentId() + RedisKey.LIKED_ANSWER_COMMENT);
          comment.setLikedCount(Integer.valueOf(likedCount + ""));
        }
        answer.setAnswerCommentList(answerCommentList);

        // 获取用户点赞状态
        Long rank = jedis.zrank(answer.getAnswerId() + RedisKey.LIKED_ANSWER, String.valueOf(userId));
        answer.setLikeState(rank == null ? false : true);
        // 获取该回答被点赞次数
        Long likedCount = jedis.zcard(answer.getAnswerId() + RedisKey.LIKED_ANSWER);
        answer.setLikedCount(Integer.valueOf(likedCount + ""));
      }

    }

    //话题信息
    Map<Integer, String> topicMap = new HashMap<>();
//    topicMap.put(12,"啊");//k
    try {
//      String temp = "{\"12\":\"啊\"}"; //objectMapper.writeValueAsString(topicMap);
      topicMap = objectMapper.readValue(question.getTopicKvList(), new TypeReference<HashMap<Integer,String>>(){});
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    map.put("topicMap", topicMap);
    map.put("question", question);
    map.put("answerList", answerList);
    map.put("followedUserList", followedUserList);

    return map;
  }

  /**
   * 问题列表（翻页
   */
  @Override
  public List<Question> listQuestionByPage(Integer curPage) {
    // 当请求没有请求页信息，则默认为第一页
    curPage = curPage == null ? 1 : curPage;
    // 每页条数
    int limit = 3;
    // 记录起始条数
    int offset = (curPage - 1) * limit;

    List<Question> questionList = new ArrayList<>();
    try (Jedis jedis = jedisPool.getResource()) {
      Set<String> idSet = jedis.zrange(RedisKey.QUESTION_SCANED_COUNT, offset, offset + limit - 1);
      List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);

      if (idList.size() > 0) {
        questionList = questionDao.listQuestionByQuestionId(idList);

        for (Question question : questionList) {
//          question.setAnswerCount(answerDao);
          question.setFollowedCount(Integer
              .parseInt(jedis.zcard(question.getQuestionId() + RedisKey.FOLLOWED_QUESTION) + ""));
        }
      }
    }

    return questionList;
  }

  /**
   * 判断某人是否关注某问题
   */
  @Override
  public boolean judgePeopleFollowQuestion(Integer userId, Integer questionId) {
    Long rank = null;
    try (Jedis jedis = jedisPool.getResource()) {
      rank = jedis.zrank(userId + RedisKey.FOLLOW_QUESTION, String.valueOf(questionId));
    }
    return rank == null ? false : true;
  }

  /**
   * 关注问题
   */
  @Override
  public boolean followQuestion(Integer userId, Integer questionId) {
    boolean status = false;
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(userId + RedisKey.FOLLOW_QUESTION, System.currentTimeMillis(),
          String.valueOf(questionId));
      jedis.zadd(questionId + RedisKey.FOLLOWED_QUESTION, System.currentTimeMillis(),
          String.valueOf(userId));
      status = true;
    }
    return status;
  }

  /**
   * 取消关注问题
   */
  @Override
  public boolean unfollowQuestion(Integer userId, Integer questionId) {
    boolean status = false;
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zrem(userId + RedisKey.FOLLOW_QUESTION, String.valueOf(questionId));
      jedis.zrem(questionId + RedisKey.FOLLOWED_QUESTION, String.valueOf(userId));
      status = true;
    }
    return status;
  }

  /**
   * 某用户关注的问题列表
   * @param userId
   * @return
   */
  @Override
  public List<Question> listFollowingQuestion(Integer userId) {
    List<Question> list = new ArrayList<Question>();
    try (Jedis jedis = jedisPool.getResource()) {
      Set<String> idSet = jedis.zrange(userId + RedisKey.FOLLOW_QUESTION, 0, -1);
      List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);

      if (idList.size() > 0) {
        list = questionDao.listQuestionByQuestionId(idList);
        for (Question question : list) {
          //todo 设置该问题的回答数

          //设置该问题关注数
          Long followedCount = jedis.zcard(question.getQuestionId() + RedisKey.FOLLOWED_QUESTION);
          question.setFollowedCount(Integer.parseInt(followedCount + ""));
        }
      }
    }
    return list;
  }

  /**
   * 某用户的提问列表
   * @param userId
   * @param curPage
   * @return
   */
  @Override
  public PageBean<Question> listQuestionByUserId(Integer userId, Integer curPage) {
    // 请求页默认为0
    curPage = curPage == null ? 1 : curPage;
    // 每页问题数
    int limit = 8;
    // 问题数坐标
    int offset = (curPage - 1) * limit;

    // 用户提问总数
    int allCount = questionDao.selectQuestionCountByUserId(userId);
    // 总页数
    int allPage = 0;
    if (allCount <= limit) {
      allPage = 1;
    } else if (allCount / limit == 0) {
      allPage = allCount / limit;
    } else {
      allPage = allCount / limit + 1;
    }

    Map<String, Object> map = new HashMap<>();
    map.put("offset", offset);
    map.put("limit", offset + limit);
    map.put("userId", userId);

    // 得到某页问题列表
    List<Question> questionList = questionDao.listQuestionByUserId(map);

    // 构造PageBean
    PageBean<Question> pageBean = new PageBean<>(allPage, curPage);
    pageBean.setList(questionList);

    return pageBean;
  }

}
