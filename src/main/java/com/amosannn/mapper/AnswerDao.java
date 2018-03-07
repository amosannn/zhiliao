package com.amosannn.mapper;

import com.amosannn.model.Answer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AnswerDao {

  String TABLE_NAME = " answer ";

  @Insert({"insert into ", TABLE_NAME, " (answer_content,create_time,question_id,user_id) values (#{answerContent},#{createTime},#{questionId},#{userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "answerId")
  Integer insertAnswer(Answer answer);


}
