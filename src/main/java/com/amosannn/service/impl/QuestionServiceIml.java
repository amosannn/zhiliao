package com.amosannn.service.impl;

import com.amosannn.mapper.QuestionDao;
import com.amosannn.mapper.TopicDao;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.Question;
import com.amosannn.model.Topic;
import com.amosannn.service.QuestionService;
import com.amosannn.util.ResponseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceIml implements QuestionService {

  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private TopicDao topicDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public Integer ask(Question question, String topicNames, Integer userId) {
    String[] topicNameArr = topicNames.split(",");
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

}
