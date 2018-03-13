package com.amosannn.mapper;

import com.amosannn.model.AnswerComment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface CommentDao {

  String ANSWER_TABLE_NAME = " answer_comment ";

  @Insert({"insert into ", ANSWER_TABLE_NAME, " (answer_comment_id, answer_comment_content, create_time, answer_id, user_id) values(#{answerCommentId}, #{answerCommentContent}, #{createTime}, #{answerId}, #{userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "answerCommentId")
  Integer insertAnswerComment(AnswerComment comment);

  @Insert({"insert into ", ANSWER_TABLE_NAME, " (answer_comment_id, answer_comment_content, create_time, answer_id, user_id, at_user_id, at_user_name) values(#{answerCommentId}, #{answerCommentContent}, #{createTime}, #{answerId}, #{userId}, #{atUserId}, #{atUserName})"})
  @Options(useGeneratedKeys = true, keyProperty = "answerCommentId")
  Integer insertAnswerCommentReply(AnswerComment comment);

}
