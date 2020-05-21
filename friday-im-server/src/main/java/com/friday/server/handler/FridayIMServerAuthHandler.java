package com.friday.server.handler;

import com.friday.server.kafka.KafkaProducerManage;
import com.friday.server.netty.UidChannelManager;
import com.friday.server.protobuf.Message.FridayMessage;
import com.friday.server.redis.ConversationRedisServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: login
 * @Author: Damon(npf)
 * @Date: 2020-05-21:10:47
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class FridayIMServerAuthHandler extends SimpleChannelInboundHandler<FridayMessage> {

    @Autowired
    private UidChannelManager uidChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Autowired
    private KafkaProducerManage kafkaProducerManage;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage fridayMessage) throws Exception {

    }
}
