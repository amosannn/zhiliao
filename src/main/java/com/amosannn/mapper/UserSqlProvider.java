package com.amosannn.mapper;

import com.amosannn.model.User;
import org.apache.ibatis.jdbc.SQL;

public class UserSqlProvider {

  /**
   * 登录验证
   * @param user
   * @return
   */
  public String selectUserIdByEmailOrUsername(final User user) {
    return new SQL(){{
      SELECT("user_id");
      FROM("user");
      if (user.getEmail() != null) {
        WHERE("email=#{email}");
      }
      if (user.getUsername() != null) {
        WHERE("username=#{username}");
      }
      if (user.getPassword() != null) {
        WHERE("password=#{password}");
      }
    }}.toString();
  }


}
