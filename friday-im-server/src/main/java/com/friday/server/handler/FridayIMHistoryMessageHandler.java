package com.friday.server.handler;

import com.friday.common.bean.im.MessageContent;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-24:10:51
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class FridayIMHistoryMessageHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        if (message.getType() == Message.FridayMessage.Type.HisMessagesReq) {
            Message.HisMessagesReq hisMessagesReq = message.getHisMessagesReq();
            log.info("received history msg req:{}", hisMessagesReq);
            List<MessageContent> contents = conversationRedisServer.getHistoryMsg(hisMessagesReq.getConverId(), hisMessagesReq.getBeginId());
            AtomicReference<List<Message.MessageContent>> messageContents = new AtomicReference<>(new ArrayList<>());
            contents.forEach(c -> messageContents.get().add(Message.MessageContent.newBuilder()
                    .setId(c.getId())
                    .setContent(c.getContent())
                    .setUid(c.getUid())
                    .setTime(c.getType())
                    .setType(Message.MessageType.valueOf(c.getType())).build()));
            long unReadCount = conversationRedisServer.getHistoryUnreadCount(hisMessagesReq.getConverId(), hisMessagesReq.getBeginId());
            Message.HisMessagesAck ack = Message.HisMessagesAck.newBuilder()
                    .setId(hisMessagesReq.getId())
                    .setConverId(hisMessagesReq.getConverId())
                    .setUnReadCount(unReadCount)
                    .addAllMessageList(messageContents.get()).build();
            Message.FridayMessage fridayMessage = Message.FridayMessage.newBuilder()
                    .setType(Message.FridayMessage.Type.HisMessagesAck)
                    .setHisMessagesAck(ack).build();
            channelHandlerContext.writeAndFlush(fridayMessage);
        } else {
            channelHandlerContext.fireChannelRead(message);
        }
    }
}
