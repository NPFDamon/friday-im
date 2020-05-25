package com.friday.route.message.executor;

import com.friday.common.netty.ServerChannelManager;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserServerRedisService;
import com.friday.route.message.processor.SingleMessageProcessor;
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
    @Autowired
    private ConversationRedisServer conversationRedisServer;

    public void sendAndSaveMsg(String message) {
        SingleMessageProcessor singleMessageProcessor = new SingleMessageProcessor(serverChannelManager, userServerRedisService, message, conversationRedisServer);
        executor.execute(singleMessageProcessor);
    }
}
