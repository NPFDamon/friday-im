package com.friday.server.kafka;

import com.friday.server.kafka.message.listener.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:12:20
 */
@Slf4j
public class KafkaMessageProcessor<K, V> implements Runnable {

    private String topic;

    private KafkaConsumer<K, V> consumer;

    private MessageListener<K, V> listener;

    private static final int MILLIS = 100;

    public KafkaMessageProcessor(String topic, KafkaConsumer<K, V> kvKafkaConsumer, MessageListener<K, V> messageListener) {
        this.topic = topic;
        this.consumer = kvKafkaConsumer;
        this.listener = messageListener;
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(MILLIS));
            if (!records.isEmpty()) {
                records.forEach(r -> {
                    log.info("current record:[{}]", records.toString());
                    listener.receive(topic, r.key(), r.value());
                });
                consumer.commitAsync();
            } else {
                sleep(MILLIS);
            }
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Thread.sleep error: ", e);
        }
    }
}
