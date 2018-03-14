package com.amosannn.service;

import com.amosannn.model.AnswerComment;
import com.amosannn.model.QuestionComment;
import java.util.Map;

public interface CommentService {

  AnswerComment commentAnswer(Integer answerId, String commentContent, Integer userId);

  AnswerComment replyAnswerComment(Map<String, Object> reqMap, Integer userId);

  QuestionComment commentQuestion(Integer questionId, String commentContent, Integer userId);

  QuestionComment replyQuestionComment(Map<String, Object> reqMap, Integer userId);

}
