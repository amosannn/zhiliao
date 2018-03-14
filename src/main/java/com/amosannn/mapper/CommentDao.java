package com.amosannn.mapper;

import com.amosannn.model.AnswerComment;
import com.amosannn.model.QuestionComment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface CommentDao {

  String ANSWER_TABLE_NAME = " answer_comment ";
  String QUESTION_TABLE_NAME = " question_comment ";

  @Insert({"insert into ", ANSWER_TABLE_NAME, " (answer_comment_content, create_time, answer_id, user_id) values(#{answerCommentContent}, #{createTime}, #{answerId}, #{userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "answerCommentId")
  Integer insertAnswerComment(AnswerComment comment);

  @Insert({"insert into ", ANSWER_TABLE_NAME, " (answer_comment_content, create_time, answer_id, user_id, at_user_id, at_user_name) values(#{answerCommentContent}, #{createTime}, #{answerId}, #{userId}, #{atUserId}, #{atUserName})"})
  @Options(useGeneratedKeys = true, keyProperty = "answerCommentId")
  Integer insertAnswerCommentReply(AnswerComment comment);

  @Insert({"insert into ", QUESTION_TABLE_NAME, " (question_comment_content,create_time,question_id,user_id) values(#{questionCommentContent},#{createTime},#{questionId},#{userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "questionCommentId")
  void insertQuestionComment(QuestionComment comment);

  @Insert({"insert into ", QUESTION_TABLE_NAME, " (question_comment_content, create_time, question_id, user_id, at_user_id, at_user_name) values(#{questionCommentContent}, #{createTime}, #{questionId}, #{userId}, #{atUserId}, #{atUserName})"})
  @Options(useGeneratedKeys = true, keyProperty = "questionCommentId")
  void insertQuestionCommentReply(QuestionComment comment);

}
