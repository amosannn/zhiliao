package com.amosannn.mapper;

import com.amosannn.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionDao {

  String TABLE_NAME = "question";
  String SELECT_FIELDS = "";

  @Insert({"insert into ", TABLE_NAME, " (question_title,question_content,topic_kv_list,create_time,user_id) values(#{question.questionTitle},#{question.questionContent},#{question.topicKvList},#{question.createTime},#{question.userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "question.questionId")
  Integer insertQuestion(@Param("question") Question question);

  @Insert("insert into question_topic(question_id,topic_id) values(#{questionId},#{topicId})")
  void insertIntoQuestionTopic(@Param("questionId") Integer questionId, @Param("topicId") Integer topicId);
}
