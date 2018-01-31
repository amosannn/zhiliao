package com.amosannn.mapper;

import com.amosannn.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDao {

  String TABLE_NAME = " user ";
  String INSERT_FIELDS = " email, password, activation_state, activation_code, join_time, username, avatar_url, gender, simple_desc, position, industry, career, education, full_desc, liked_count, collected_count, follow_count, followed_count, scaned_count, weibo_user_id ";
  String SELECT_FILEDS = " user_id " + INSERT_FIELDS;

  @Select({"select ", " count(*) from ", TABLE_NAME, " where email like #{email}"})
  int emailCount(String email) ;

  @Insert({"insert into ", TABLE_NAME, " (email, password, activation_code, join_time, username, avatar_url", ") values (#{email}, #{password}, #{activationCode}, #{joinTime}, #{username}, #{avatarUrl})"})
  void insertUser(User user);

  @SelectProvider(type = UserSqlProvider.class, method = "selectUserIdByEmailOrUsername")
  Integer selectUserIdByEmailOrUsername(User user);

  @Select({"select activation_state from ", TABLE_NAME, " where user_id = #{userId}"})
  Integer selectActivationStateByUserId(@Param("userId") Integer userId);

  @Update({"update ", TABLE_NAME, " set activation_state = 1 where activation_code = #{activationCode}"})
  void updateActivationStateByActivationCode(String activationCode);

  @Select({"select ", SELECT_FILEDS, " from ", TABLE_NAME, " where user_id = #{userId}"})
  User selectProfileInfoByUserId(Integer userId);
}
