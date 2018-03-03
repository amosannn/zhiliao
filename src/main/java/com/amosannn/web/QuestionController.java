package com.amosannn.web;

import com.amosannn.model.Question;
import com.amosannn.service.QuestionService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    String topicNames = (String) map.get("topicNames");
    Integer questionId = questionService.ask(question, topicNames, userId);
    return ResponseResult.createSuccessResult("提问发布成功！", "question id :" + questionId);
  }

  @RequestMapping("question/{questionId}")
  public ResponseResult<Map<String, Object>> questionDetail(@PathVariable Integer questionId,
      HttpServletRequest request) {
    Integer userId = userService.getUserIdFromRedis(request);
    Map<String, Object> questionDetail = questionService.questionDetail(questionId, userId);

    // todo 收藏夹信息

    return ResponseResult.createSuccessResult("获取问题详情页成功！", questionDetail);
  }

//  @RequestMapping("/questionList")
//  public String questionList(){
//    return
//  }

  @RequestMapping("/listQuestionByPage")
  public ResponseResult<List<Question>> listQuestionByPage(@RequestBody Map<String, Object> map){
    Integer curPage = Integer.parseInt(map.get("curPage")+"");
    List<Question> questionList = questionService.listQuestionByPage(curPage);

    return ResponseResult.createSuccessResult("获取第" + curPage + "页问题列表成功", questionList);
  }


}
