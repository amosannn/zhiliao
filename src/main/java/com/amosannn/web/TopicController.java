package com.amosannn.web;

import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import com.amosannn.util.ResponseResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topic")
public class TopicController {

  @Resource
  TopicService service;

  @RequestMapping(value = "/topicCount")
  public int topicCount() {
    return service.topicCount();
  }

  @RequestMapping("/listBaseTopic")
  public ResponseResult<Map<String, Object>> listBaseTopic() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("baseTopic", service.listBaseTopic());
    return ResponseResult.createSuccessResult("请求成功", map);
  }

  @RequestMapping(value = "/topicDesc", method = RequestMethod.POST)
  public ResponseResult<Map<String, Topic>> topicDetail(@RequestBody Map<String, Integer> map) {
    Integer topicId = map.get("topicId");
    return ResponseResult.createSuccessResult("请求成功", service.topicDetail(topicId));
  }

  @RequestMapping("/listTopicByParentId")
  public ResponseResult<Map<String, List<Topic>>> listTopicByParentId(
      @RequestBody Map<String, Integer> map) {
    Integer topicId = map.get("topicId");
    return ResponseResult.createSuccessResult("请求成功", service.listTopicByParentId(topicId));
  }
}
