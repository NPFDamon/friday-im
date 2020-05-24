package com.friday.server.handler;

import com.friday.common.bean.im.MessageContent;
import com.friday.common.bean.im.UserConversation;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.protobuf.Message.*;
import com.friday.common.redis.ConversationRedisServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-24:11:12
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class FridayIMConversationHandler extends SimpleChannelInboundHandler<FridayMessage> {

    @Autowired
    private UidChannelManager uidChannelManager;
    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage message) {
        if (message.getType() == FridayMessage.Type.ConverReq) {
            ConverReq req = message.getConverReq();
            log.info("received conversation req:{}", req);
            if (req.getType() == OperationType.DETAIL) {
                UserConversation userConversation = conversationRedisServer.getConversationListInfo(
                        uidChannelManager.getIdByChannel(channelHandlerContext.channel()), req.getConversationId());
                if (null == userConversation) {
                    sendFailAck(channelHandlerContext, req.getId());
                }
                assert userConversation != null;
                ConverInfo info = buildConversationInfo(userConversation);

                ConverAck converAck = getConverAck(req, info);

                FridayMessage fridayMessage = FridayMessage.newBuilder()
                        .setType(FridayMessage.Type.ConverAck)
                        .setConverAck(converAck).build();
                channelHandlerContext.writeAndFlush(fridayMessage);
            } else if (req.getType() == OperationType.ALL) {
                List<UserConversation> converList = conversationRedisServer
                        .getConversationListByUid(uidChannelManager.getIdByChannel(channelHandlerContext.channel()));

                List<ConverInfo> converInfos = converList.stream().map(this::buildConversationInfo).collect(Collectors.toList());

                ConverAck converAck = getConverAck(req, converInfos);

                FridayMessage fridayMessage = FridayMessage.newBuilder()
                        .setType(FridayMessage.Type.ConverAck)
                        .setConverAck(converAck).build();
                channelHandlerContext.writeAndFlush(fridayMessage);
            }
        } else {
            channelHandlerContext.fireChannelRead(message);
        }
    }

    private ConverAck getConverAck(ConverReq req, List<ConverInfo> converInfos) {
        return ConverAck.newBuilder()
                .setId(req.getId())
                .setCode(Code.SUCCESS)
                .setTime(System.currentTimeMillis())
                .addAllConverList(converInfos)
                .build();
    }

    private ConverAck getConverAck(ConverReq req, ConverInfo info) {
        return ConverAck.newBuilder()
                .setId(req.getId())
                .setCode(Code.SUCCESS)
                .setTime(System.currentTimeMillis())
                .setConverInfo(info)
                .build();
    }

    private void sendFailAck(ChannelHandlerContext ctx, Long id) {
        ConverAck converAck = ConverAck.newBuilder()
                .setId(id)
                .setCode(Code.OPERATION_TYPE_INVALID)
                .setTime(System.currentTimeMillis())
                .build();
        FridayMessage fridayMessage = FridayMessage.newBuilder()
                .setType(FridayMessage.Type.ConverAck)
                .setConverAck(converAck).build();
        ctx.writeAndFlush(fridayMessage);
    }

    private ConverInfo buildConversationInfo(UserConversation userConversation) {
        ConverInfo.Builder builder = ConverInfo.newBuilder();
        builder.setConverId(userConversation.getId());
        builder.setType(ConverType.valueOf(userConversation.getType()));
        builder.addAllUidList(userConversation.getUidList());
        if (ConverType.valueOf(userConversation.getType()) == ConverType.GROUP) {
            builder.setGroupId(userConversation.getGroupId());
        }
        MessageContent msgContent = userConversation.getMessageContent();
        Message.MessageContent content = Message.MessageContent.newBuilder().setId(msgContent.getId())
                .setUid(msgContent.getUid())
                .setType(Message.MessageType.valueOf(msgContent.getType()))
                .setContent(msgContent.getContent())
                .setTime(msgContent.getTime())
                .build();
        builder.setLastContent(content);
        builder.setReadMsgId(userConversation.getReadMsgId());
        return builder.build();
    }
}
