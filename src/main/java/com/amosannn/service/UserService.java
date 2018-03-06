package com.amosannn.service;

import com.amosannn.model.User;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

  Map<String, String> register(String username, String email, String password);

  Map<String, String> login(String username, String email, String password, HttpServletResponse response);

  void activate(String activationCode);

  void logout(HttpServletRequest request, HttpServletResponse response);

  Integer getUserIdFromRedis(HttpServletRequest request);

  Map<String, Object> profile(Integer userId, Integer localUserId);

  boolean judgePeopleFollowUser(Integer localUserId, Integer userId);

  boolean followUser(Integer localUserId, Integer userId);

  boolean unfollowUser(Integer localUserId, Integer userId);

  List<User> listFollowingUser(Integer userId);

  List<User> listFollowedUser(Integer userId);
}
