package com.amosannn.service.impl;

import com.amosannn.mapper.AnswerDao;
import com.amosannn.model.Answer;
import com.amosannn.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerServiceImpl implements AnswerService{

  @Autowired
  AnswerDao answerDao;

  @Override
  public Integer answer(Answer answer, Integer userId) {
    answer.setUserId(userId);
    answer.setCreateTime(System.currentTimeMillis());
    Integer answerId = answerDao.insertAnswer(answer);

//    answer.setAnswerId(answerId);

    return answerId;
  }

}
