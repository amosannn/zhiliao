package com.amosannn.service;

import com.amosannn.model.AnswerComment;
import java.util.Map;

public interface CommentService {

  AnswerComment commentAnswer(Integer answerId, String commentContent, Integer userId);

  AnswerComment replyAnswerComment(Map<String, Object> reqMap, Integer userId);

}
