package com.amosannn.mapper;

import com.amosannn.model.Collection;
import com.amosannn.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CollectionDao {

  String SELECT_TABLE = " collection ";

  @Insert({"insert into ", SELECT_TABLE, " (collection_name,create_time,update_time,user_id) values(#{collectionName},#{createTime},#{updateTime},#{userId})"})
  @Options(useGeneratedKeys = true, keyProperty = "collection.collectionId")
  Integer insertCollection(Collection collection);

  @Select({"select collection_id,collection_name,create_time,update_time,followed_count from ", SELECT_TABLE, " where user_id = #{userId}"})
  List<Collection> listCreatingCollectionByUserId(@Param("userId") Integer userId);

  @Select({"select user_id from ", SELECT_TABLE, " where collection_id = #{collectionId}"})
  Integer selectUserIdByCollectionId(@Param("collectionId") Integer collectionId);

  @Select("select collection_id, collection_name, create_time, update_time from collection where collection_id = #{collectionId}")
  @Results({
      @Result(column = "collection_id", property = "collectionId"),
      @Result(column = "collection_name", property = "collectionName"),
      @Result(column = "create_time", property = "createTime"),
      @Result(column = "update_time", property = "updateTime"),
      @Result(column = "user_id", property = "user",
          one = @One(select = "selectUserByUserId"))
  })
  Collection selectCollectionByCollectionId(@Param("collectionId") Integer collectionId);

  @Select("select user_id, username, avatar_url, simple_desc from user where user_id = #{userId}")
  User selectUserByUserId(@Param("userId") Integer userId);
}
