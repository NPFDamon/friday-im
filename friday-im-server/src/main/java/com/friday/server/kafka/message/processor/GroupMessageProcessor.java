package com.friday.server.kafka.message.processor;

import com.friday.common.netty.UidChannelManager;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserServerRedisService;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-31:9:59
 */
public class GroupMessageProcessor extends MessageProcessor implements Runnable{
    public GroupMessageProcessor(UserServerRedisService userServerRedisService, String message,
                                 ConversationRedisServer conversationRedisServer, UidChannelManager uidChannelManager) {
        super(userServerRedisService, message, conversationRedisServer, uidChannelManager);
    }

    @Override
    public void run() {
        super.init();
    }
}
