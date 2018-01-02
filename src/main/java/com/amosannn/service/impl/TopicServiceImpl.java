package com.amosannn.service.impl;

import com.amosannn.mapper.TopicDao;
import com.amosannn.model.Topic;
import com.amosannn.service.TopicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicDao dao;

    @Override
    public int topicCount() {
        return dao.topicCount();
    }

    @Override
    public List<Topic> listBaseTopic() {
        return dao.listBaseTopic();
    }

}