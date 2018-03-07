package com.amosannn.mapper;

import com.amosannn.model.Topic;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TopicDao {

  String TABLE_NAME = " topic ";
  String INSERT_FIELDS = " topic_name, topic_desc, topic_image, parent_topic_id, followed_count ";
  String SELECT_FIELDS = " topic_id, " + INSERT_FIELDS;

  // 话题数量
  @Select({"select count(T.topic_id) FROM ", TABLE_NAME ," T"})
  int topicCount();

  // 已关注话题数
  int followedTopicCount();

  // 话题广场基础话题
  @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where parent_topic_id=0"})
  List<Topic> listBaseTopic();

  @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " order by followed_count desc limit 0,5"})
  List<Topic> listHotTopic();

  // 话题广场子话题（分页
  Map<String, String> listTopic();

  // 话题详情
  @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where topic_id = #{topicId} "})
  Topic topicDetail(Integer topicId);

  // 父话题
  @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where parent_topic_id = #{topicId} "})
  List<Topic> listTopicByParentId(@Param("topicId") Integer topicId);

  // 正在关注的话题
  List<Topic> listFollowingTopic(List<Integer> idList);

  // 查找话题对应的id
  @Select({"select topic_id from ", TABLE_NAME, " where topic_name = #{topicName} limit 0,1"})
  Integer selectTopicIdByTopicName(@Param("topicName") String topicName);


  // 插入话题
  @Insert({"insert into ", TABLE_NAME, " (topic_name,parent_topic_id,topic_desc) values(#{topicName},#{parentTopicId},'暂无描述')"})
  @Options(useGeneratedKeys = true, keyProperty = "topic.topicId")
  Integer insertTopic(Topic topic);

  // 更新话题关注者数量
  @Update({"update ", TABLE_NAME, " set followed_count = followed_count + #{addCount} where topic_id = #{topicId} ",})
  void updateFollowedCount(@Param("topicId") Integer topicId, @Param("addCount") Integer addCount);

  // 获取该话题下的问题列表
  @Select("select question_id from question_topic where topic_id = #{topicId}")
  List<Integer> selectQuestionIdByTopicId(Integer topicId);

  @Select({"select * from ", TABLE_NAME, " where topic_name REGEXP #{topicName} "})
  List<Topic> listTopicByTopicName(@Param("topicName") String topicName);
}
