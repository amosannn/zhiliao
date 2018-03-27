package com.amosannn.mapper;

import com.amosannn.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageDao {

  @Select("insert into message(type,second_type,message_date,message_time,from_user_id,from_user_name,question_id,question_title,answer_id,comment_id,user_id) "
      + " values(#{type},#{secondType},#{messageDate},#{messageTime},#{fromUserId},#{fromUserName},#{questionId},#{questionTitle},#{answerId},#{commentId},#{userId})")
  void insertTypeComment(Message message);


}
