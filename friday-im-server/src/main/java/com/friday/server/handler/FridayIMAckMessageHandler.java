package com.friday.server.handler;

import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message.*;
import com.friday.common.redis.ConversationRedisServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-24:10:42
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class FridayIMAckMessageHandler extends SimpleChannelInboundHandler<FridayMessage> {
    @Autowired
    private UidChannelManager uidChannelManager;
    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage message) throws Exception {
        if (message.getType() == FridayMessage.Type.MessageAck) {
            MessageAck ack = message.getMessageAck();
            log.info("received message ack:{}", ack);
            String uid = uidChannelManager.getIdByChannel(channelHandlerContext.channel());
            conversationRedisServer.deleteWaitUserAckMsg(uid, ack.getConverId(), ack.getId());
            conversationRedisServer.updateUserReadMessageId(uid, ack.getConverId(), ack.getId());
        } else {
            channelHandlerContext.fireChannelRead(message);
        }
    }
}
