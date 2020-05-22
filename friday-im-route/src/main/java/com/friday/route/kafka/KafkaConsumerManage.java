package com.friday.route.kafka;

import com.friday.route.message.listener.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (C),Damon
 *
 * @Description: kafka consumer manage
 * @Author: Damon(npf)
 * @Date: 2020-05-17:10:21
 */
@Component
@Slf4j
public class KafkaConsumerManage<K, V> {

    private Properties properties = new Properties();

    @Autowired(required = false)
    private List<MessageListener> messageListeners;

    private Map<String, MessageListener> messageListenerMap = new HashMap<>();

    @PostConstruct
    public void start() {
        log.info(">>>>>>>>>>>>>>>>>>>>> kafka consumer manager start..... <<<<<<<<<<<<<<<<<<<<<<<<<<<");
        consumerProperties();
        this.analyzeMessageListeners(messageListeners);
        for (Map.Entry<String, MessageListener> entry : messageListenerMap.entrySet()) {
            String topic = entry.getKey();
            MessageListener messageListener = entry.getValue();
            KafkaConsumer<K, V> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Collections.singletonList(topic));
            KafkaMessageProcessor<K, V> messageProcessor = new KafkaMessageProcessor<K, V>(topic, consumer, messageListener);
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(messageProcessor);
        }

    }

    private void analyzeMessageListeners(List<MessageListener> messageListeners) {
        if (!CollectionUtils.isEmpty(messageListeners)) {
            for (MessageListener messageListener : messageListeners) {
                String topic = messageListener.getTopic();
                if (StringUtils.isBlank(topic)) {
                    log.warn("MessageListener:{} topic is null",
                            messageListener.getClass().getCanonicalName());
                    continue;
                }
                messageListenerMap.put(topic, messageListener);
            }
        }else {
            log.error("message listener is null !");
        }
    }

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private String autoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private String autoCommitInterval;

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    private void consumerProperties() {
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
    }

}
