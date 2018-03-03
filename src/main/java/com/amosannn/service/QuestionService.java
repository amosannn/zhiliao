package com.amosannn.service;

import com.amosannn.model.Question;
import java.util.List;
import java.util.Map;

public interface QuestionService {

  Integer ask(Question question, String topicNames, Integer userId);

  Map<String, Object> questionDetail(Integer questionId, Integer userId);

  List<Question> listQuestionByPage(Integer curPage);
}
