package com.amosannn.web;

import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import com.amosannn.util.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Resource
    TopicService service;

    @RequestMapping(value = "/topicCount")
    public int topicCount(){
        return service.topicCount();
    }

    @RequestMapping("/listBaseTopic")
    public ResponseResult<List<Topic>> listBaseTopic(){
        service.listBaseTopic();
        return ResponseResult.createSuccessResult("请求成功", service.listBaseTopic());
    }
}
