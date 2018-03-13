package com.amosannn.web;

import com.amosannn.model.Collection;
import com.amosannn.service.CollectionService;
import com.amosannn.service.UserService;
import com.amosannn.util.ResponseResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhiliao")
public class CollectionController {

  @Autowired
  CollectionService collectionService;
  @Autowired
  UserService userService;

  /**
   * 新增收藏夹
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/addCollection")
  public ResponseResult<Integer> addCollection(@RequestBody Map<String, Object> map,
      HttpServletRequest request) {
    Collection collection = new Collection();
    Integer userId = userService.getUserIdFromRedis(request);
    String collectionName = String.valueOf(map.get("collectionName"));
    collection.setCollectionName(collectionName);
    Integer collectionId = collectionService.addCollection(collection, userId);
    if (collectionId > 0) {
      return ResponseResult.createSuccessResult("新建收藏夹成功！", collectionId);
    }
    return ResponseResult.createFailResult("新建收藏夹失败！", -1);
  }

  /**
   * 获取某个收藏夹的详情
   * @param collectionId
   * @param request
   * @return
   */
  @RequestMapping("/collection/{collectionId}")
  public ResponseResult<Map<String, Object>> collection(@PathVariable Integer collectionId,
      HttpServletRequest request) {
    // 本地用户Id
    Integer localUserId = userService.getUserIdFromRedis(request);
    // 获取目标收藏夹内答案列表
    Map<String, Object> map = collectionService.getCollectionContent(collectionId, localUserId);
    // 获取当前用户收藏夹列表
    List<Collection> collectionList = collectionService.listCreatingCollection(localUserId);
    map.put("collectionList", collectionList);
    return ResponseResult.createSuccessResult("获取收藏夹详情成功", map);
  }

  /**
   * 获取我的收藏夹列表
   * @param request
   * @return
   */
  @RequestMapping("/listCreatingCollection")
  public ResponseResult<Map<String, Object>> listCreatingCollection(HttpServletRequest request) {
    Integer userId = userService.getUserIdFromRedis(request);
    List<Collection> collectionList = collectionService.listCreatingCollection(userId);
    Map<String, Object> map = new HashMap<>();
    map.put("collectionList", collectionList);
    return ResponseResult.createSuccessResult("获取我的收藏夹列表成功！",  map);
  }

  /**
   * 判断是否收藏了某回答
   * @param map
   * @return
   */
  @RequestMapping("/collectionContainAnswer")
  public ResponseResult<Boolean> collectionContainAnswer(@RequestBody Map<String, Object> map) {
    Integer collectionId = Integer.parseInt(map.get("collectionId") + "");
    Integer answerId = Integer.parseInt(map.get("answerId") + "");
    Boolean status = collectionService.collectionContainAnswer(collectionId, answerId);
    if (status) {
      return ResponseResult.createSuccessResult("收藏夹包含该问题！", status);
    }
    return ResponseResult.createFailResult("收藏夹不包含该问题！", status);
  }

  /**
   * 判断某用户是否关注某收藏夹
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/judgePeopleFollowCollection")
  public ResponseResult<Boolean> judgePeopleFollowCollection(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    Integer collectionId = Integer.parseInt(map.get("collectionId") + "");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = collectionService.judgePeopleFollowCollection(userId, collectionId);
    if (status) {
      return ResponseResult.createSuccessResult("该用户正在关注该收藏夹！", status);
    }
    return ResponseResult.createFailResult("该用户并未关注该收藏夹！", status);
  }

  /**
   * 收藏回答
   * @param map
   * @return
   */
  @RequestMapping("/collectAnswer")
  public ResponseResult<Boolean> collectAnswer(@RequestBody Map<String, Object> map) {
    Integer collectionId = Integer.parseInt(map.get("collectionId") + "");
    Integer answerId = Integer.parseInt(map.get("answerId") + "");
    Boolean status = collectionService.collectAnswer(collectionId, answerId);
    if (status) {
      return ResponseResult.createSuccessResult("收藏回答成功！", status);
    }
    return ResponseResult.createFailResult("收藏回答失败！", status);
  }

  /**
   * 取消收藏回答
   * @param map
   * @return
   */
  @RequestMapping("/ uncollectAnswer")
  public ResponseResult<Boolean> uncollectAnswer(@RequestBody Map<String, Object> map) {
    Integer collectionId = Integer.parseInt(map.get("collectionId") + "");
    Integer answerId = Integer.parseInt(map.get("answerId") + "");
    Boolean status = collectionService.uncollectAnswer(collectionId, answerId);
    if (status) {
      return ResponseResult.createSuccessResult("取消收藏回答成功！", status);
    }
    return ResponseResult.createFailResult("取消收藏回答失败！", status);
  }

  /**
   * 关注收藏夹
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/followCollection")
  public ResponseResult<Boolean> followCollection(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    Integer collectionId = Integer.parseInt(map.get("collectionId")+"");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = collectionService.followCollection(userId, collectionId);
    if (status) {
      return ResponseResult.createSuccessResult("关注成功", status);
    }
    return ResponseResult.createFailResult("关注失败", status);
  }

  /**
   * 取消关注收藏夹
   * @param map
   * @param request
   * @return
   */
  @RequestMapping("/unfollowCollection")
  public ResponseResult<Boolean> unfollowCollection(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    Integer collectionId = Integer.parseInt(map.get("collectionId")+"");
    Integer userId = userService.getUserIdFromRedis(request);
    Boolean status = collectionService.unfollowCollection(userId, collectionId);
    if (status) {
      return ResponseResult.createSuccessResult("取消关注成功", status);
    }
    return ResponseResult.createFailResult("取消关注失败", status);
  }
}
