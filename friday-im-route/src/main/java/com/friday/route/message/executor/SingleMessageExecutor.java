package com.friday.route.message.executor;

import com.friday.route.message.processor.SingleMessageProcessor;
import com.friday.route.redis.UserServerRedisService;
import com.friday.server.netty.ServerChannelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:12:19
 */
@Slf4j
@Component
public class SingleMessageExecutor {
    private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    @Autowired
    private ServerChannelManager serverChannelManager;
    @Autowired
    private UserServerRedisService userServerRedisService;

    public void sendAndSaveMsg(String message) {
        SingleMessageProcessor singleMessageProcessor = new SingleMessageProcessor(serverChannelManager, userServerRedisService, message);
        executor.execute(singleMessageProcessor);
    }
}
