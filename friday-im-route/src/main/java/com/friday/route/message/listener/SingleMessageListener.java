package com.friday.route.message.listener;

import com.friday.route.message.executor.SingleMessageExecutor;
import com.friday.server.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:12:18
 */
public class SingleMessageListener extends MessageListener<String, String> {

    @Autowired
    private SingleMessageExecutor singleMessageExecutor;

    public SingleMessageListener() {
        this.setTopic(Constants.KAFKA_TOPIC_SINGLE);
    }

    @Override
    public void receive(String topic, String key, String message) {
        singleMessageExecutor.sendAndSaveMsg(message);
    }
}
