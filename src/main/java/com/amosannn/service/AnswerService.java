package com.amosannn.service;

import com.amosannn.model.Answer;
import java.util.Map;

public interface AnswerService {

  Integer answer(Answer answer, Integer userId);

  void likeAnswer(Integer answerId, Integer userId);

  void unlikeAnswer(Integer answerId, Integer userId);

  Map<String, Object> listTodayHotAnswer();

  Map<String, Object> listMonthHotAnswer();
}
