package com.amosannn.service;

import com.amosannn.model.PageBean;
import com.amosannn.model.Question;
import java.util.List;
import java.util.Map;

public interface QuestionService {

  Integer ask(Question question, String topicNames, Integer userId);

  Map<String, Object> getQuestionDetail(Integer questionId, Integer userId);

  List<Question> listQuestionByPage(Integer curPage);

  boolean judgePeopleFollowQuestion(Integer userId, Integer questionId);

  boolean followQuestion(Integer userId, Integer questionId);

  boolean unfollowQuestion(Integer userId, Integer questionId);

  List<Question> listFollowingQuestion(Integer userId);

  PageBean<Question> listQuestionByUserId(Integer userId, Integer curPage);
}
