package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.mapper.QuestionDao;
import com.amosannn.mapper.TopicDao;
import com.amosannn.model.Answer;
import com.amosannn.model.PageBean;
import com.amosannn.model.Question;
import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import java.util.ArrayList;
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

  @Autowired
  private TopicDao topicDao;

  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private AnswerDao answerDao;

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
  public Map<String, Object> topicDetail(Integer topicId, Integer curPage, String tabType, Integer userId) {
    Map<String, Object> map = new HashMap<>();
    // 获取话题信息
    Topic topic = topicDao.topicDetail(topicId);

    try (Jedis jedis = jedisPool.getResource()) {
      // 获得关注该话题的人数
      Long followedCount = jedis.zcard(topicId + RedisKey.FOLLOWED_TOPIC);
      topic.setFollowedCount(Integer.parseInt(followedCount + ""));

      if ("hot".equals(tabType)) {
        // 热门
        // 获得该话题下答案列表的分页数据
        List<Integer> questionIdList = topicDao.selectQuestionIdByTopicId(topicId);

        if (questionIdList.size() > 0) {
          PageBean<Answer> pageBean = _listGoodAnswerByQuestionId(questionIdList, curPage, jedis, userId);
          map.put("pageBean", pageBean);
        } else {
          map.put("pageBean", new PageBean<Answer>());
        }
      } else if ("dynamic".equals(tabType)) {
        // 最新回答
        // 获得该话题下答案列表的分页数据
        List<Integer> questionIdList = topicDao.selectQuestionIdByTopicId(topicId);

        if (questionIdList.size() > 0) {
          PageBean<Answer> pageBean = _listNewAnswerByQuestionId(questionIdList, curPage, jedis, userId);
          map.put("pageBean", pageBean);
        } else {
          map.put("pageBean", new PageBean<Answer>());
        }
      } else if ("allQuestion".equals(tabType)) {
        // 全部问题
        PageBean<Question> pageBean = _listAllQuestionByTopicId(topic.getTopicId(), curPage, jedis);
        map.put("pageBean", pageBean);
        // 告诉页面返回的是问题列表，而不是答案列表
        map.put("tabType", "allQuestion");
      }
    }
    map.put("topic", topic);
    return map;
  }

  @Override
  public Map<String, List<Topic>> listParentTopic(Integer topicId) {
    List<Topic> topics = topicDao.listParentTopic(topicId);
    Map<String, List<Topic>> map = new HashMap<>();
    map.put("parentTopics", topics);
    return map;
  }

  @Override
  public Map<String, List<Topic>> listTopicByParentId(Integer topicId) {
    List<Topic> topics = topicDao.listTopicByParentId(topicId);
    Map<String, List<Topic>> map = new HashMap<>();
    map.put("childTopics", topics);
    return map;
  }

  @Override
  public List<Topic> listTopicByTopicName(String topicName) {
    List<Topic> map = new ArrayList<>();
    List<Topic> topicList = topicDao.listTopicByTopicName(topicName);
    return topicList;
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
      topicDao.updateFollowedCount(topicId, 1);
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
      topicDao.updateFollowedCount(topicId, -1);
      status = true;
    }
    return status;
  }

  /**
   * 判断用户是否关注过该话题
   * @param userId
   * @param topicId
   * @return
   */
  @Override
  public boolean judgePeopleFollowTopic(Integer userId, Integer topicId){
    Long rank = null;
    try (Jedis jedis = jedisPool.getResource()) {
      rank = jedis.zrank(userId + RedisKey.FOLLOW_TOPIC, String.valueOf(topicId));
    }
    return rank == null ? false : true;
  }

  /**
   * 获取热门回答
   * @param questionIdList
   * @param curPage
   * @param jedis
   * @param userId
   * @return
   */
  private PageBean<Answer> _listGoodAnswerByQuestionId(List<Integer> questionIdList, Integer curPage, Jedis jedis, Integer userId) {
    // 能执行该函数，说明questionIdList不空
    // 当请求页数为空时
    curPage = curPage == null ? 1 : curPage;
    // 每页记录数，从哪开始
    int limit = 8;
    int offset = (curPage - 1) * limit;
    // 获得总记录数，总页数
    int allCount = answerDao.listAnswerCountByQuestionId(questionIdList);
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
    map.put("questionIdList", questionIdList);
    // 得到某页数据列表
    List<Answer> answerList = answerDao.listGoodAnswerByQuestionId(map);

    for (Answer answer : answerList) {
      // 获取用户点赞状态
      Long rank = jedis.zrank(answer.getAnswerId() + RedisKey.LIKED_ANSWER, String.valueOf(userId));
      answer.setLikeState(rank == null ? false : true);
    }
    // 构造PageBean
    PageBean<Answer> pageBean = new PageBean<>(allPage, curPage);
    pageBean.setList(answerList);

    return pageBean;
  }

  /**
   * 获取最近回答
   * @param questionIdList
   * @param curPage
   * @param jedis
   * @param userId
   * @return
   */
  private PageBean<Answer> _listNewAnswerByQuestionId(List<Integer> questionIdList, Integer curPage, Jedis jedis, Integer userId) {
    // 能执行该函数，说明questionIdList不空
    // 当请求页数为空时
    curPage = curPage == null ? 1 : curPage;
    // 每页记录数，从哪开始
    int limit = 8;
    int offset = (curPage - 1) * limit;
    // 获得总记录数，总页数
    int allCount = answerDao.listAnswerCountByQuestionId(questionIdList);
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
    map.put("questionIdList", questionIdList);
    // 得到某页数据列表
    List<Answer> answerList = answerDao.listNewAnswerByQuestionId(map);

    for (Answer answer : answerList) {
      // 获取用户点赞状态
      Long rank = jedis.zrank(answer.getAnswerId() + RedisKey.LIKED_ANSWER, String.valueOf(userId));
      answer.setLikeState(rank == null ? false : true);
    }
    // 构造PageBean
    PageBean<Answer> pageBean = new PageBean<>(allPage, curPage);
    pageBean.setList(answerList);

    return pageBean;
  }

  /**
   * 获取话题下的所有问题
   * @param topicId
   * @param curPage
   * @param jedis
   * @return
   */
  private PageBean<Question> _listAllQuestionByTopicId(Integer topicId, Integer curPage, Jedis jedis) {
    // 当请求页数为空时
    curPage = curPage == null ? 1 : curPage;
    // 每页记录数，从哪开始
    int limit = 8;
    int offset = (curPage - 1) * limit;
    // 获得总记录数，总页数
    int allCount = questionDao.selectQuestionCountByTopicId(topicId);
    int allPage = 0;
    if (allCount <= limit) {
      allPage = 1;
    } else if (allCount / limit == 0) {
      allPage = allCount / limit;
    } else {
      allPage = allCount / limit + 1;
    }

    // 构造查询map
    Map<String, Integer> map = new HashMap<>();
    map.put("offset", offset);
    map.put("limit", offset + limit);
    map.put("topicId", topicId);
    // 得到某页数据列表
    List<Integer> questionIdList = questionDao.listQuestionIdByTopicId(map);
    System.out.println(questionIdList);
    List<Question> questionList = new ArrayList<Question>();
    if (questionIdList.size() > 0) {
      questionList = questionDao.listQuestionByQuestionId(questionIdList);
      for (Question question : questionList) {
        Long followedCount = jedis.zcard(question.getQuestionId() + RedisKey.FOLLOWED_QUESTION);
        question.setFollowedCount(Integer.parseInt(followedCount + ""));
      }
    }

    // 构造PageBean
    PageBean<Question> pageBean = new PageBean<>(allPage, curPage);
    pageBean.setList(questionList);

    return pageBean;
  }


}
