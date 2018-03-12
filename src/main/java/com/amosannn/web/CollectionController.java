package com.amosannn.web;

import com.amosannn.model.Collection;
import com.amosannn.service.CollectionService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhiliao")
public class CollectionController {

  @Autowired
  CollectionService collectionService;
  @Autowired
  UserService userService;

  @RequestMapping("/collection/{collectionId}")
  public ResponseResult<Map<String, Object>> collection(@PathVariable Integer collectionId,
      HttpServletRequest request) {
    // 本地用户Id
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取收藏夹内答案列表
    Map<String, Object> map = collectionService.getCollectionContent(collectionId, localUserId);
    // 获取当前用户收藏夹列表
    List<Collection> collectionList = collectionService.listCreatingCollection(localUserId);
    map.put("collectionList", collectionList);
    return ResponseResult.createSuccessResult("获取收藏夹详情成功", map);
  }

  @RequestMapping("/followCollection")
  public ResponseResult<Boolean> followCollection(Map<String, Object> map, HttpServletRequest request) {
    Integer collectionId = Integer.parseInt(map.get("collectionId")+"");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = collectionService.followCollection(userId, collectionId);
    if (status) {
      return ResponseResult.createSuccessResult("关注成功", status);
    }
    return ResponseResult.createFailResult("关注失败", status);
  }

  @RequestMapping("/unfollowCollection")
  public ResponseResult<Boolean> unfollowCollection(Map<String, Object> map, HttpServletRequest request) {
    Integer collectionId = Integer.parseInt(map.get("collectionId")+"");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = collectionService.unfollowCollection(userId, collectionId);
    if (status) {
      return ResponseResult.createSuccessResult("取消关注成功", status);
    }
    return ResponseResult.createFailResult("取消关注失败", status);
  }
}
