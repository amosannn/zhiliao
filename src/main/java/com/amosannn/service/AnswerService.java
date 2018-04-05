package com.amosannn.service;

import com.amosannn.model.Answer;
import com.amosannn.model.PageBean;
import java.util.List;
import java.util.Map;

public interface AnswerService {

  Integer answer(Answer answer, Integer userId);

  Map<String, Object> getAnswerDetail(Integer answerId, Integer userId);

  PageBean<Answer> listAnswerByUserId(Integer userId, Integer curPage);

  void likeAnswer(Integer answerId, Integer userId);

  void unlikeAnswer(Integer answerId, Integer userId);

  Map<String, Object> listTodayHotAnswer();

  Map<String, Object> listMonthHotAnswer();

  boolean judgePeopleLikedAnswer(Integer localUserId, Integer answerId);
}
