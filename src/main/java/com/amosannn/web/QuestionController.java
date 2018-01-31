package com.amosannn.web;

import com.amosannn.model.Question;
import com.amosannn.service.QuestionService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhiliao")
public class QuestionController {

  @Autowired
  private QuestionService questionService;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @RequestMapping("/ask")
  public ResponseResult<String> ask(@RequestBody Map<String, Object> map,
      HttpServletRequest request) {
    Question question = new Question();
    Integer userId = userService.getUserIdFromRedis(request);
    Map<String, String> resMap = (Map<String, String>) map.get("question");
    question.setQuestionTitle(resMap.get("questionTitle"));
    question.setQuestionContent(resMap.get("questionContent"));
    String topicNames = (String)map.get("topicNames");
    Integer questionId = questionService.ask(question, topicNames, userId);
    return ResponseResult.createSuccessResult("提问发布成功！", "question id :" + questionId);
  }
}
