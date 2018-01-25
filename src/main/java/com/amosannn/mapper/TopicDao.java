package com.amosannn.mapper;

import com.amosannn.model.Topic;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
