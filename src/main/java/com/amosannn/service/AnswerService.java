package com.amosannn.service;

import com.amosannn.model.Answer;

public interface AnswerService {

  Integer answer(Answer answer, Integer userId);

  void likeAnswer(Integer answerId, Integer userId);

  void unlikeAnswer(Integer answerId, Integer userId);
}
