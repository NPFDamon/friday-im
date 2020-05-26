package com.friday.route.kafka.message.listener;

import com.friday.route.kafka.message.executor.SingleMessageExecutor;
import com.friday.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:12:18
 */
@Component
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
