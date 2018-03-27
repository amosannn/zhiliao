package com.amosannn.service;

import com.amosannn.model.AnswerComment;
import com.amosannn.model.QuestionComment;
import java.util.List;
import java.util.Map;

public interface CommentService {

  AnswerComment commentAnswer(Integer answerId, String commentContent, Integer userId);

  AnswerComment replyAnswerComment(Map<String, Object> reqMap, Integer userId);

  QuestionComment commentQuestion(Integer questionId, String commentContent, Integer userId);

  QuestionComment replyQuestionComment(Map<String, Object> reqMap, Integer userId);

  Boolean likeAnswerComment(Integer userId, Integer answerCommentId);

  Boolean likeQuestionComment(Integer userId, Integer questionCommentId);

  Integer getAnswerCommentCount(Integer answerId);

  List<AnswerComment> listAnswerComment(Integer answerId);
}
