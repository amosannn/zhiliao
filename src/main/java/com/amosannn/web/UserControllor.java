package com.amosannn.web;

import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
  public ResponseResult<Map<String, String>> login(@RequestBody Map<String, String> reqMap, HttpServletResponse res) {
    String username = reqMap.get("username");
    String email = reqMap.get("email");
    String password = reqMap.get("password");
    Map<String, String> resMap = userService.login(username, email, password, res);
    if (resMap.get("login-error") != null) {
      return ResponseResult.createLoginFailResult(resMap);
    }

    return ResponseResult.createSuccessResult("登录成功!", resMap);
  }

  
}
