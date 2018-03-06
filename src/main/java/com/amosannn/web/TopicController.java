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
  public ResponseResult<Map<String, Object>> topicDetail(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    Integer userId = userService.getUserIdFromRedis(request);

    Integer topicId = Integer.parseInt(map.get("topicId")+"");
    Integer curPage = Integer.parseInt(map.get("curPage")+"");
    Boolean allQuestion = Boolean.parseBoolean(map.get("allQuestion")+"");// todo 需测试是否"false"可以转换成false
    return ResponseResult.createSuccessResult("请求成功", topicService.topicDetail(topicId, curPage, allQuestion, userId));
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
   * 取关话题
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

  /**
   * 判断用户是否关注过某话题
   * @param topicId
   * @param request
   * @return
   */
  @RequestMapping("/judgePeopleFollowTopic")
  public ResponseResult<Boolean> judgePeopleFollowTopic(Integer topicId, HttpServletRequest request) {
    // 获取用户信息
    Integer userId = userService.getUserIdFromRedis(request);
    // 判断用户是否关注过该话题
    boolean status = topicService.judgePeopleFollowTopic(userId, topicId);
    if (status) {
      return ResponseResult.createSuccessResult("已关注过该话题！", status);
    }
    return ResponseResult.createFailResult("未关注过该话题！", status);
  }

}
