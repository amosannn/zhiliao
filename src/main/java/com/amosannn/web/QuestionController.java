package com.amosannn.web;

import com.amosannn.model.Question;
import com.amosannn.service.QuestionService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
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

  /**
   * 提问
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/ask")
  public ResponseResult<Map<String, Object>> ask(@RequestBody Map<String, Object> map,
      HttpServletRequest request) {
    Question question = new Question();
    Integer userId = userService.getUserIdFromRedis(request);
    Map<String, String> questionMap = (Map<String, String>) map.get("question");
    question.setQuestionTitle(questionMap.get("questionTitle"));
    question.setQuestionContent(questionMap.get("questionContent"));
    String topicNames = (String) map.get("topicNames");
    Integer questionId = questionService.ask(question, topicNames, userId);
    Map<String, Object> resMap = new HashMap<>();
    resMap.put("questionId", questionId);
    return ResponseResult.createSuccessResult("提问发布成功！", resMap);
  }

  /**
   * 获取某问题详情
   * @param questionId
   * @param request
   * @return
   */
  @RequestMapping("question/{questionId}")
  public ResponseResult<Map<String, Object>> questionDetail(@PathVariable Integer questionId,
      HttpServletRequest request) {
    Integer userId = userService.getUserIdFromRedis(request);
    Map<String, Object> questionDetail = questionService.getQuestionDetail(questionId, userId);

    // todo 收藏夹信息

    return ResponseResult.createSuccessResult("获取问题详情页成功！", questionDetail);
  }

//  @RequestMapping("/questionList")
//  public String questionList(){
//    return
//  }

  /**
   * 获取问题列表（分页）
   * @param map
   * @return
   */
  @RequestMapping("/listQuestionByPage")
  public ResponseResult<List<Question>> listQuestionByPage(@RequestBody Map<String, Object> map) {
    Integer curPage = Integer.parseInt(map.get("curPage") + "");
    List<Question> questionList = questionService.listQuestionByPage(curPage);

    return ResponseResult.createSuccessResult("获取第" + curPage + "页问题列表成功", questionList);
  }

  /**
   * 判断用户是否关注过该问题
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/judgePeopleFollowQuestion")
  public ResponseResult<Boolean> judgePeopleFollowQuestion(@RequestBody Map<String, Object> map,
      HttpServletRequest request) {
    Integer questionId = Integer.parseInt(map.get("questionId") + "");
    Integer userId = userService.getUserIdFromRedis(request);
    boolean status = questionService.judgePeopleFollowQuestion(userId, questionId);
    if (status) {
      return ResponseResult.createSuccessResult("已关注该问题", status);
    }
    return ResponseResult.createSuccessResult("未关注该问题", status);
  }

  /**
   * 关注该问题
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/followQuestion")
  public ResponseResult<Boolean> followQuestion(@RequestBody Map<String, Object> map,
      HttpServletRequest request) {
    Integer questionId = Integer.parseInt(map.get("questionId") + "");
    Integer userId = userService.getUserIdFromRedis(request);
    boolean status = questionService.followQuestion(userId, questionId);
    if (status) {
      return ResponseResult.createSuccessResult("关注成功", status);
    }
    return ResponseResult.createFailResult("关注失败", status);
  }

  /**
   * 取消关注该问题
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/unfollowQuestion")
  public ResponseResult<Boolean> unfollowQuestion(@RequestBody Map<String, Object> map,
      HttpServletRequest request) {
    Integer questionId = Integer.parseInt(map.get("questionId") + "");
    Integer userId = userService.getUserIdFromRedis(request);
    boolean status = questionService.unfollowQuestion(userId, questionId);
    if (status) {
      return ResponseResult.createSuccessResult("取消关注成功", status);
    }
    return ResponseResult.createFailResult("取消关注失败", status);
  }
}
