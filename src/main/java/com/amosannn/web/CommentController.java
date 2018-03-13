package com.amosannn.web;

import com.amosannn.model.AnswerComment;
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
   * 回复评论
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
}
