package com.amosannn.mapper;

import com.amosannn.model.Answer;
import com.amosannn.model.Question;
import com.amosannn.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface AnswerDao {

  String TABLE_NAME = " answer ";

  @Insert({"insert into ", TABLE_NAME,
      " (answer_content,create_time,question_id,user_id) values (#{answerContent},#{createTime},#{questionId},#{userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "answerId")
  Integer insertAnswer(Answer answer);

  @Update({"update ", TABLE_NAME,
      " set liked_count = liked_count + #{addCount} where answer_id = #{answerId}"})
  void updateLikedCount(@Param("answerId") Integer answerId, @Param("addCount") Integer addCount);

//  @Select("select a.answer_id,a.answer_content,a.liked_count,a.create_time,"
//      + " q.question_id,q.question_title,u.user_id,u.username,u.avatar_url,u.simple_desc "
//      + " from answer a "
//      + " join question q on a.question_id = q.question_id "
//      + " join user u on a.user_id = u.user_id "
//      + " where a.create_time > #{createTime} "
//      + " order by a.liked_count desc,a.create_time desc "
//      + " limit 0,10")
  @Select("select answer_id, answer_content, liked_count, create_time, question_id, user_id from answer where create_time > #{createTime} order by liked_count desc, create_time desc limit 0,10")
  @Results({
      @Result(id = true, column = "answer_id", property = "answerId", javaType = Integer.class),
      @Result(column = "answer_content", property = "answerContent"),
      @Result(column = "liked_count", property = "likedCount", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(column = "create_time", property = "createTime"),
      @Result(column = "question_id", property = "question", javaType = Question.class,
          one = @One(select = "selectQuestionById")),
      @Result(column = "user_id", property = "user",
          one = @One(select = "selectUserById"))
  })
  List<Answer> listAnswerByCreateTime(@Param("createTime") long createTime);

  @Select("select question_id, question_title from question where question_id = #{questionId}")
  @ResultType(Question.class)
  Question selectQuestionById(@Param("questionId") Integer questionId);

  @Select("select user_id, username, avatar_url, simple_desc from user where user_id = #{userId}")
  @ResultType(User.class)
  User selectUserById(@Param("userId") Integer userId);
}
