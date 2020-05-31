package com.friday.server.kafka.message.executor;

import com.friday.common.netty.ServerChannelManager;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserServerRedisService;
import com.friday.server.kafka.message.processor.SingleMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-31:10:33
 */
@Component
public class SingleMessageExecutor {
    private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    @Autowired
    private ServerChannelManager serverChannelManager;
    @Autowired
    private UserServerRedisService userServerRedisService;
    @Autowired
    private ConversationRedisServer conversationRedisServer;
    @Autowired
    private UidChannelManager uidChannelManager;

    public void sendAndSaveMsg(String message) {
        SingleMessageProcessor singleMessageProcessor =
                new SingleMessageProcessor(userServerRedisService, message, conversationRedisServer, uidChannelManager);
        executor.execute(singleMessageProcessor);
    }
}
