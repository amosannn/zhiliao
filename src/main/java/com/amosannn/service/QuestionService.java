package com.amosannn.service;

import com.amosannn.model.Question;

public interface QuestionService {

  public Integer ask(Question question, String topicNames, Integer userId);
}
