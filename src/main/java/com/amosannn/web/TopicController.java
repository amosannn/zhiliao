package com.amosannn.web;

import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhiliao")
public class TopicController {

  @Autowired
  TopicService topicService;
  @Autowired
  UserService userService;

  @RequestMapping(value = "/topic/topicCount")
  public int topicCount() {
    return topicService.topicCount();
  }

  @GetMapping("/topic/topics")
  public ResponseResult<Map<String, List<Topic>>> listAllTopic() {
    Map<String, List<Topic>> map = topicService.listAllTopic();
    return ResponseResult.createSuccessResult("请求成功", map);
  }

  @RequestMapping(value = "/topic/topicDetail", method = RequestMethod.POST)
  public ResponseResult<Map<String, Topic>> topicDetail(@RequestBody Map<String, Integer> map) {
    Integer topicId = map.get("topicId");
    return ResponseResult.createSuccessResult("请求成功", topicService.topicDetail(topicId));
  }

  /**
   * 子话题列表
   * @param map
   * @return
   */
  @RequestMapping("/topic/listTopicByParentId")
  public ResponseResult<Map<String, List<Topic>>> listTopicByParentId(
      @RequestBody Map<String, Integer> map) {
    Integer topicId = map.get("topicId");
    return ResponseResult.createSuccessResult("请求成功", topicService.listTopicByParentId(topicId));
  }

  /**
   * 关注话题
   * @param topicId
   * @param request
   * @return
   */
  @RequestMapping("/followTopic")
  public ResponseResult<Boolean> followTopic(Integer topicId, HttpServletRequest request) {
    Integer userId = userService.getUserIdFromRedis(request);
    boolean status = topicService.followTopic(userId, topicId);
    if (status) {
      return ResponseResult.createSuccessResult("关注成功！", status);
    }
    return ResponseResult.createFailResult("关注失败！", status);
  }

  /**
   * 关注话题
   * @param topicId
   * @param request
   * @return
   */
  @RequestMapping("/unfollowTopic")
  public ResponseResult<Boolean> unfollowTopic(Integer topicId, HttpServletRequest request) {
    Integer userId = userService.getUserIdFromRedis(request);
    boolean status = topicService.unfollowTopic(userId, topicId);
    if (status) {
      return ResponseResult.createSuccessResult("取关成功！", status);
    }
    return ResponseResult.createFailResult("取关失败！", status);
  }


}
