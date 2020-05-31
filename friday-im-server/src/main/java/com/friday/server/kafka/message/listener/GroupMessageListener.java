package com.friday.server.kafka.message.listener;

import com.friday.common.constant.Constants;
import com.friday.server.kafka.message.executor.GroupMessageExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-31:9:59
 */
@Component
public class GroupMessageListener extends MessageListener<String, String> {

    @Autowired
    private GroupMessageExecutor groupMessageExecutor;

    public GroupMessageListener() {
        this.setTopic(Constants.KAFKA_TOPIC_GROUP);
    }

    @Override
    public void receive(String topic, String key, String message) {
        groupMessageExecutor.sendAndSaveMsg(message);
    }
}
