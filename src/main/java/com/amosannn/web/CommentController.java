package com.amosannn.web;

import com.amosannn.model.AnswerComment;
import com.amosannn.model.QuestionComment;
import com.amosannn.service.CommentService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhiliao")
public class CommentController {

  @Autowired
  private CommentService commentService;
  @Autowired
  private UserService userService;

  /**
   * 评论回答
   * @param reqMap
   * @param request
   * @return
   */
  @RequestMapping("/commentAnswer")
  public ResponseResult<Map<String, Object>> commentAnswer(@RequestBody Map<String, Object> reqMap,
      HttpServletRequest request) {
    Map<String, Object> resMap = new HashMap<>();
    Integer answerId = Integer.parseInt(reqMap.get("answerId") + "");
    String commentContent = String.valueOf(reqMap.get("answerCommentContent"));
    Integer userId = userService.getUserIdFromRedis(request);
    AnswerComment comment = commentService.commentAnswer(answerId, commentContent, userId);
    resMap.put("answerComment", comment);
    return ResponseResult.createSuccessResult("评论成功！", resMap);
  }

  /**
   * 回复回答下的评论
   * @param reqMap
   * @param request
   * @return
   */
  @RequestMapping("/replyAnswerComment")
  public ResponseResult<Map<String, Object>> replyAnswerComment(@RequestBody Map<String, Object> reqMap,
      HttpServletRequest request) {
    Map<String, Object> resMap = new HashMap<>();

    Integer userId = userService.getUserIdFromRedis(request);
    AnswerComment answerComment = commentService.replyAnswerComment(reqMap, userId);
    resMap.put("answerComment", answerComment);
    return ResponseResult.createSuccessResult("回复评论成功！", resMap);
  }

  /**
   * 点赞回答下的评论
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/likeAnswerComment")
  public ResponseResult<Boolean> likeAnswerComment(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    Integer answerCommentId = Integer.parseInt(map.get("answerCommentId") + "");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = commentService.likeAnswerComment(userId, answerCommentId);
    if (status) {
      return ResponseResult.createSuccessResult("点赞成功！", status);
    }
    return ResponseResult.createFailResult("点赞失败！", status);
  }

  /**
   * 评论问题
   * @param reqMap
   * @param request
   * @return
   */
  @RequestMapping("/commentQuestion")
  public ResponseResult<Map<String, Object>> commentQuestion(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) {
    Map<String, Object> resMap = new HashMap<>();
    Integer questionId = Integer.parseInt(reqMap.get("questionId") + "");
    String commentContent = String.valueOf(reqMap.get("commentContent"));
    Integer userId = userService.getUserIdFromRedis(request);
    QuestionComment questionComment = commentService.commentQuestion(questionId, commentContent, userId);
    resMap.put("questionComment", questionComment);
    return ResponseResult.createSuccessResult("评论问题成功！", resMap);
  }

  /**
   * 回复问题下的评论
   * @param reqMap
   * @param request
   * @return
   */
  @RequestMapping("/replyQuestionComment")
  public ResponseResult<Map<String, Object>> replyQuestionComment(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) {
    Map<String, Object> resMap = new HashMap<>();
    Integer userId = userService.getUserIdFromRedis(request);
    QuestionComment questionComment = commentService.replyQuestionComment(reqMap, userId);
    resMap.put("questionComment", questionComment);
    return ResponseResult.createSuccessResult("回复评论成功！", resMap);
  }

  /**
   * 点赞问题下的评论
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/likeQuestionComment")
  public ResponseResult<Boolean> likeQuestionComment(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    Integer questionCommentId = Integer.parseInt(map.get("questionCommentId") + "");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = commentService.likeQuestionComment(userId, questionCommentId);
    if (status) {
      return ResponseResult.createSuccessResult("点赞成功！", status);
    }
    return ResponseResult.createFailResult("点赞失败！", status);
  }

}
