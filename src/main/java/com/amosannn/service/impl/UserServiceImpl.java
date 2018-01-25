package com.amosannn.service.impl;

import com.amosannn.mapper.UserDao;
import com.amosannn.model.User;
import com.amosannn.service.UserService;
import com.amosannn.util.MyUtil;
import com.amosannn.util.RedisKey;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
      map.put("register-error", "邮箱格式错误");
      return map;
    }

    // 检验用户名长度
    if (StringUtils.isEmpty(username) || username.length() > 10) {
      map.put("register-error", "用户名长度需在1-10个字符");
      return map;
    }

    // 检验密码长度
    matcher = passwordPattern.matcher(password);
    if (!matcher.matches()) {
      map.put("register-error", "密码长度需在6-20个字符");
      return map;
    }

    //检测邮箱可用性
    if (userDao.emailCount(email) > 0) {
      map.put("register-error", "该邮箱已被注册");
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

    map.put("register-success", "注册成功");
    return map;
  }

  @Override
  public Map<String, String> login(String username, String email, String password) {
    Map<String, String> map = new HashMap<>();
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      map.put("login-error", "用户名、密码不可为空");
      return map;
    }

    if (StringUtils.isEmpty(username) || username.length() > 10) {
      map.put("login-error", "用户名格式错误");
      return map;
    }

    Matcher matcher = emailPattern.matcher(email);
    if (!matcher.matches()) {
      map.put("login-error", "邮箱格式错误");
      return map;
    }

    matcher = passwordPattern.matcher(password);
    if (!matcher.matches()) {
      map.put("login-error", "密码格式错误");
      return map;
    }

    MyUtil.md5(password);



  }

}
