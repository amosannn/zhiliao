package com.amosannn.web;

import com.amosannn.model.Question;
import com.amosannn.model.Topic;
import com.amosannn.model.User;
import com.amosannn.service.QuestionService;
import com.amosannn.service.TopicService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhiliao")
public class UserControllor {

  @Autowired
  private UserService userService;
  @Autowired
  private TopicService topicService;
  @Autowired
  private QuestionService questionService;

  @RequestMapping("/register")
  public ResponseResult<Map<String, String>> register(@RequestBody Map<String, String> reqMap) {
    String username = reqMap.get("username");
    String email = reqMap.get("email");
    String password = reqMap.get("password");
    Map<String, String> resMap = userService.register(username, email, password);
    if (resMap.get("register-success") != null) {
      return ResponseResult.createSuccessResult("邮件已送出，激活邮箱后即可登录～", resMap);
    }
    return ResponseResult.createRegisterFailResult(resMap);
  }

  @RequestMapping("/login")
  public ResponseResult<Map<String, String>> login(@RequestBody Map<String, String> reqMap,
      HttpServletResponse res) {
    String username = reqMap.get("username");
    String email = reqMap.get("email");
    String password = reqMap.get("password");
    Map<String, String> resMap = userService.login(username, email, password, res);
    if (resMap.get("login-error") != null) {
      return ResponseResult.createLoginFailResult(resMap);
    }

    return ResponseResult.createSuccessResult("登录成功!", resMap);
  }

  @RequestMapping("/activate")
  public ResponseResult<Map<String, String>> activate(@RequestBody Map<String, String> map) {
    String activationCode = map.get("code");
    userService.activate(activationCode);
    return ResponseResult.createSuccessResult("账户已激活!", null);
  }

  @RequestMapping("/logout")
  public ResponseResult<Map<String, String>> logout(HttpServletRequest request,
      HttpServletResponse response) {
    userService.logout(request, response);
    return ResponseResult.createSuccessResult("账户已登出!", null);
  }

  @RequestMapping("/profile/{userId}")
  public ResponseResult<Map<String, Object>> profile(@PathVariable Integer userId, Integer page,
      HttpServletRequest request) {
    Map<String, Object> profileMap = new HashMap<>();
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取用户信息
    profileMap = userService.profile(userId, localUserId);

    // todo listAnswerByUserId

    return ResponseResult.createSuccessResult("请求成功!", profileMap);
  }

  @RequestMapping("/profileQuestion/{userId}")
  public ResponseResult<Map<String, Object>> profileQuestion(@PathVariable Integer userId,
      Integer curPage, HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取用户信息
    Map<String, Object> profileMap = userService.profile(userId, localUserId);
    // 获取提问列表
    List<Question> questionList = questionService.listQuestionByUserId(userId, curPage);
    // 装载提问列表
    profileMap.put("questionList", questionList);

    return ResponseResult.createSuccessResult("用户主页提问模块装载成功！", profileMap);
  }

  @RequestMapping("/judgePeopleFollowUser")
  public ResponseResult<String> judgePeopleFollowUser(Integer userId,
      HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    boolean status = userService.judgePeopleFollowUser(localUserId, userId);
    if (!status) {
      return ResponseResult.createSuccessResult("未关注该用户!", String.valueOf(status));
    }
    return ResponseResult.createSuccessResult("已关注该用户！", String.valueOf(status));
  }

  @RequestMapping("/follow")
  public ResponseResult<String> followUser(Integer userId, HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    boolean status = userService.followUser(localUserId, userId);
    if (!status) {
      return ResponseResult.createSuccessResult("关注失败!", String.valueOf(status));
    }
    return ResponseResult.createSuccessResult("关注成功!", String.valueOf(status));
  }

  @RequestMapping("/unfollow")
  public ResponseResult<String> unfollowUser(Integer userId, HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    boolean status = userService.unfollowUser(localUserId, userId);
    if (!status) {
      return ResponseResult.createSuccessResult("取关失败!", String.valueOf(status));
    }
    return ResponseResult.createSuccessResult("取关成功!", String.valueOf(status));
  }

  @RequestMapping("/profileFollowPeople/{userId}")
  public ResponseResult<Map<String, Object>> profileFollowPeople(@PathVariable Integer userId,
      HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取用户信息
    Map<String, Object> profileMap = userService.profile(userId, localUserId);
    // 获取关注者列表
    List<User> followingUserList = userService.listFollowingUser(userId);
    profileMap.put("followingUserList", followingUserList);

    return ResponseResult.createSuccessResult("获取关注列表成功！", profileMap);
  }

  @RequestMapping("/profileFollowedPeople/{userId}")
  public ResponseResult<Map<String, Object>> profileFollowedPeople(@PathVariable Integer userId,
      HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取用户信息
    Map<String, Object> profileMap = userService.profile(userId, localUserId);
    // 获取粉丝列表
    List<User> followedUserList = userService.listFollowedUser(userId);
    profileMap.put("followingUserList", followedUserList);

    return ResponseResult.createSuccessResult("获取粉丝列表成功！", profileMap);
  }

  @RequestMapping("/profileFollowQuestion/{userId}")
  public ResponseResult<Map<String, Object>> profileFollowQuestion(@PathVariable Integer userId,
      HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取用户信息
    Map<String, Object> profileMap = userService.profile(userId, localUserId);
    // 获取问题列表
    List<Question> questionList = questionService.listFollowingQuestion(userId);
    profileMap.put("questionList", questionList);
    return ResponseResult.createSuccessResult("获取提问列表成功！", profileMap);
  }

  @RequestMapping("/profileFollowTopic/{userId}")
  public ResponseResult<Map<String, Object>> profileFollowTopic(@PathVariable Integer userId,
      HttpServletRequest request) {
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取用户信息
    Map<String, Object> profileMap = userService.profile(userId, localUserId);
    // 获取问题列表
    List<Topic> topicList = topicService.listFollowingTopic(userId);
    profileMap.put("questionList", topicList);
    return ResponseResult.createSuccessResult("获取提问列表成功！", profileMap);
  }
}
