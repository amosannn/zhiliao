package com.amosannn.mapper;

import com.amosannn.model.Question;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface QuestionDao {

  String TABLE_NAME = "question";
  String SELECT_FIELDS = " question_title, question_content, topic_kv_list, followed_count, scaned_count, create_time, user_id ";

  @Insert({"insert into ", TABLE_NAME, " (question_title,question_content,topic_kv_list,create_time,user_id) values(#{question.questionTitle},#{question.questionContent},#{question.topicKvList},#{question.createTime},#{question.userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "questionId")
  Integer insertQuestion(@Param("question") Question question);

  @Insert("insert into question_topic(question_id,topic_id) values(#{questionId},#{topicId})")
  void insertIntoQuestionTopic(@Param("questionId") Integer questionId, @Param("topicId") Integer topicId);

  @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where question_id = #{questionId}"})
  Question selectQuestionByQuestionId(@Param("questionId") Integer questionId);

  @Select({"select ", SELECT_FIELDS, ""})
  List<Question> listRelatedQuestion(@Param("questionId") Integer questionId);

  @SelectProvider(type = QuestionSqlProvider.class, method = "listQuestionByQuestionId")
  List<Question> listQuestionByQuestionId(@Param("idList") List<Integer> idList);

  @Select({"select ", " count(*) from ", TABLE_NAME, " where user_id = #{userId}"})
  Integer selectQuestionCountByUserId(@Param("userId") Integer userId);

  @Select({"select ", " question_id,question_title,create_time from ", TABLE_NAME, " where user_id = #{userId} limit #{offset},#{limit} "})
  List<Question> listQuestionByUserId(Map map);

  @Select("select count(*) from question_topic where topic_id = #{topicId}")
  Integer selectQuestionCountByTopicId(Integer topicId);

  @Select("select question_id from question_topic where topic_id = #{topicId} order by qt_id desc limit #{offset},#{limit}")
  List<Integer> listQuestionIdByTopicId(Map<String, Integer> map);
}
