package com.amosannn.web;

import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.HashMap;
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
    Map<String, Object> map = new HashMap<>();
    Integer localUserId = userService.getUserIdFromRedis(request);
    map = userService.profile(userId, localUserId);

    // todo listAnswerByUserId

    return ResponseResult.createSuccessResult("请求成功!", map);
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
}
