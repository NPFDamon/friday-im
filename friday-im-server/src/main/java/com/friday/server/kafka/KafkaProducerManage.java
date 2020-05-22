package com.friday.server.kafka;

import com.friday.common.bean.resVo.Result;
import com.friday.common.enums.ResultCode;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Copyright (C),Damon
 *
 * @Description: kafka producer
 * @Author: Damon(npf)
 * @Date: 2020-05-17:10:20
 */
@Component
@Slf4j
public class KafkaProducerManage {
    private static Producer<String, String> producer;

    @PostConstruct
    public void init() {
        producer = new KafkaProducer<String, String>(producerProperties());
    }

    public Result send(String topic, String key, String message) {
        if (Strings.isNullOrEmpty(topic) || message == null) {
            log.error("param error! topic:[{}], key:[{}],message:[{}]", topic, key, message);
            return Result.failure(ResultCode.COMMON_KAFKA_PRODUCE_ERROR);
        }
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
        Future<RecordMetadata> future = producer.send(record, (recordMetadata, e) -> {
            if (e != null) {
                log.error("send message to topic:{} error", topic, e);
            } else {
                log.info("send message to topic:{} success, current offset:{}, messageStr:{}", topic, recordMetadata.hasOffset(), message);
            }
        });
        Result result = Result.success();
        try {
            result.setData(future.get());
        } catch (Exception e) {
            result = Result.failure(ResultCode.COMMON_KAFKA_PRODUCE_ERROR);
            log.error("produce message error", e);
        }
        return result;
    }

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.retries}")
    private String retries;

    @Value("${spring.kafka.producer.buffer-memory}")
    private String bufferMemory;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    private Properties producerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.ACKS_CONFIG, acks);
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, retries);
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return properties;
    }
}
