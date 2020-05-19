package com.friday.route.message.listener;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:12:17
 */
public abstract class MessageListener <K, V> {
    public String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public abstract void receive(String topic, K key, V message);
}
