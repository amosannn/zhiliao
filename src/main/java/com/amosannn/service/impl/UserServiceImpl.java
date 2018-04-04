package com.amosannn.service.impl;

import com.amosannn.configuration.RedisCacheConfiguration;
import com.amosannn.mapper.UserDao;
import com.amosannn.model.User;
import com.amosannn.service.UserService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private JedisPool jedisPool;

  private static Pattern emailPattern = Pattern
      .compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
  private static Pattern passwordPattern = Pattern.compile("^\\w{6,20}$");

  @Override
  public Map<String, String> register(String username, String email, String password) {
    Map<String, String> map = new HashMap<>();

    // 检验邮箱格式
    Matcher matcher = emailPattern.matcher(email);
    if (!matcher.matches()) {
      map.put("register_error", "邮箱格式错误");
      return map;
    }

    // 检验用户名长度
    if (StringUtils.isEmpty(username) || username.length() > 10) {
      map.put("register_error", "用户名长度需在1-10个字符");
      return map;
    }

    // 检验密码长度
    matcher = passwordPattern.matcher(password);
    if (!matcher.matches()) {
      map.put("register_error", "密码长度需在6-20个字符");
      return map;
    }

    //检测邮箱可用性
    if (userDao.emailCount(email) > 0) {
      map.put("register_error", "该邮箱已被注册");
      return map;
    }

    User user = new User();
    user.setEmail(email);
    user.setUsername(username);
    user.setPassword(MyUtil.md5(password));

    // 默认头像
    user.setAvatarUrl(
        "https://avatars3.githubusercontent.com/u/16012509?s=400&u=6fe0dd08943216aeff2d3c9d1b8c3e602f6de8e9&v=4");
    user.setJoinTime(System.currentTimeMillis());
    user.setSimpleDesc("猜猜我是谁～");

    // 用户状态默认未激活
    String activateCode = MyUtil.createRadomCode();
    user.setActivationCode(activateCode);

    // 发送激活邮件

    // 数据库插入用户数据
    userDao.insertUser(user);

    // 设置默认关注用户
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(user.getUserId() + RedisKey.FOLLOW_USER, System.currentTimeMillis(),
          String.valueOf(3));
      jedis.zadd(3 + RedisKey.FOLLOWED_USER, System.currentTimeMillis(),
          String.valueOf(user.getUserId()));
      jedis.zadd(user.getUserId() + RedisKey.FOLLOWED_USER, System.currentTimeMillis(),
          String.valueOf(4));
      jedis.zadd(4 + RedisKey.FOLLOWED_TOPIC, System.currentTimeMillis(),
          String.valueOf(user.getUserId()));
    }
    jedisPool.close();

    map.put("register_success", "注册成功");
    return map;
  }

  @Override
  public Map<String, String> login(String username, String email, String password,
      HttpServletResponse response) {
    Map<String, String> map = new HashMap<>();
    Matcher matcher = null;
    if (StringUtils.isEmpty(username) && StringUtils.isEmpty(email)) {
      map.put("login_error", "用户名或邮箱不可为空");
      return map;
    }

    if (StringUtils.isEmpty(password)) {
      map.put("login_error", "密码不可为空");
      return map;
    }

    if (!StringUtils.isEmpty(email)) {
      matcher = emailPattern.matcher(email);
      if (!matcher.matches()) {
        map.put("login_error", "邮箱格式错误");
        return map;
      }
      username = null;
    } else{
      if (StringUtils.isEmpty(username) || username.length() > 10){
        map.put("login_error", "用户名格式错误");
        return map;
      }
      email = null;
    }

    matcher = passwordPattern.matcher(password);
    if (!matcher.matches()) {
      map.put("login_error", "密码格式错误");
      return map;
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(MyUtil.md5(password));
    user.setEmail(email);
    Integer userId = userDao.selectUserIdByEmailOrUsername(user);

    if (userId == null) {
      map.put("login_error", "用户名密码错误");
      return map;
    }

    // 检验用户账号是否激活
    Integer activationState = userDao.selectActivationStateByUserId(userId);
    if (1 != activationState) {
      map.put("login_error", "用户未激活");
      return map;
    }

    // 设置登录cookie
    String loginToken = MyUtil.createRadomCode();
    Cookie cookie = new Cookie("loginToken", loginToken);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 7);
    response.addCookie(cookie);

    // 存入redis
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.set(loginToken, userId.toString(), "NX", "EX", 60 * 60 * 24 * 7);
    }
//    jedisPool.close();

    map.put("login_success", "登录成功");
    // 临时方案  前端页面 token 验证
    map.put("token", loginToken);
    return map;
  }

  @Override
  public void activate(String activationCode) {
    userDao.updateActivationStateByActivationCode(activationCode);
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String loginToken = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if ("loginToken".equals(cookie.getName())) {
        loginToken = cookie.getValue();
        try (Jedis jedis = jedisPool.getResource()) {
          jedis.del(loginToken);
        }
//        jedisPool.close();
        break;
      }
    }

    Cookie cookie = new Cookie("loginToken", "");
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 30);
    response.addCookie(cookie);
  }

  @Override
  public Integer getUserIdFromRedis(HttpServletRequest request) {
    String loginToken = null;
    String userId = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if ("loginToken".equals(cookie.getName())) {
        loginToken = cookie.getValue();
        break;
      }
    }
    try (Jedis jedis = jedisPool.getResource()) {
      userId = jedis.get(loginToken);
    }
//    jedisPool.close();

    return Integer.parseInt(userId);
  }

  @Override
  public Map<String, Object> profile(Integer userId, Integer localUserId) {
    Map<String, Object> map = new HashMap<>();
    User user = userDao.selectProfileInfoByUserId(userId);

    if (userId.equals(localUserId)) {
      map.put("isSelf", true);
    } else {
      // todo 访客访问量 + 1
      map.put("isSelf", false);
    }

    try (Jedis jedis = jedisPool.getResource()) {
      Long followCount = jedis.zcard(userId + RedisKey.FOLLOW_USER);
      Long followedCount = jedis.zcard(userId + RedisKey.FOLLOWED_USER);
      Long followTopicCount = jedis.zcard(userId + RedisKey.FOLLOW_TOPIC);
      Long followQuestionCount = jedis.zcard(userId + RedisKey.FOLLOW_QUESTION);
      Long followCollectionCount = jedis.zcard(userId + RedisKey.FOLLOW_COLLECTION);
      user.setFollowCount(Integer.parseInt(followCount+""));
      user.setFollowedCount(Integer.parseInt(followedCount+""));
      user.setFollowTopicCount(Integer.parseInt(followTopicCount+""));
      user.setFollowQuestionCount(Integer.parseInt(followQuestionCount+""));
      user.setFollowCollectionCount(Integer.parseInt(followCollectionCount+""));
    }

    map.put("user", user);
    return map;
  }

  @Override
  public boolean judgePeopleFollowUser(Integer localUserId, Integer userId) {
    Long rank = null;
    try (Jedis jedis = jedisPool.getResource()){
      rank = jedis.zrank(localUserId + RedisKey.FOLLOW_USER, String.valueOf(userId));
    }
//    jedisPool.close();

    return rank == null ? false:true;
  }

  @Override
  public boolean followUser(Integer localUserId, Integer userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zadd(localUserId + RedisKey.FOLLOW_USER, System.currentTimeMillis(),
          String.valueOf(userId));
      jedis.zadd(userId + RedisKey.FOLLOWED_USER, System.currentTimeMillis(),
          String.valueOf(localUserId));
    } catch (JedisException e) {
      return false;
    }
//    jedisPool.close();

    // todo 站内信(message)

    return true;
  }

  @Override
  public boolean unfollowUser(Integer localUserId, Integer userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.zrem(localUserId + RedisKey.FOLLOW_USER, String.valueOf(userId));
      jedis.zrem(userId + RedisKey.FOLLOWED_USER, String.valueOf(localUserId));
    } catch (JedisException e) {
      return false;
    }
//    jedisPool.close();
    return true;
  }

  /**
   * 正在关注的用户列表
   * @param userId
   * @return
   */
  @Override
  public List<User> listFollowingUser(Integer userId) {
    List<User> userList = new ArrayList<User>();
    try (Jedis jedis = jedisPool.getResource()) {
      // 获取所关注用户的信息
      Set<String> idSet = jedis.zrange(userId + RedisKey.FOLLOW_USER, 0, -1);
      List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
      if (idList.size() > 0) {
        userList = userDao.listUserInfoByUserId(idList);
      }
    }
    return userList;
  }

  /**
   * 粉丝列表
   * @param userId
   * @return
   */
  @Override
  public List<User> listFollowedUser(Integer userId) {
    List<User> userList = new ArrayList<User>();
    try (Jedis jedis = jedisPool.getResource()) {
      // 获取粉丝的信息
      Set<String> idSet = jedis.zrange(userId + RedisKey.FOLLOWED_USER, 0, -1);
      List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
      if (idList.size() > 0) {
        userList = userDao.listUserInfoByUserId(idList);
      }
    }
    return userList;
  }



}
