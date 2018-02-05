package com.amosannn.mapper;

import com.amosannn.model.User;
import java.util.List;
import org.apache.ibatis.jdbc.SQL;

public class UserSqlProvider {

  /**
   * 登录验证
   */
  public String selectUserIdByEmailOrUsername(final User user) {
    return new SQL() {{
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

  public String listUserInfoByUserId(final List<Integer> userIdList) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(");
    for (Integer userId : userIdList) {
      stringBuilder.append("'" + userId + "',");
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    stringBuilder.append(")");
    return new SQL() {{
      SELECT(" user_id,username,avatar_url,simple_desc ");
      FROM("user");
      WHERE("userId in " + stringBuilder.toString());
    }}.toString();
  }


}
