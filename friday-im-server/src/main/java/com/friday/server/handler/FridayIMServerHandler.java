package com.friday.server.handler;

import com.friday.common.constant.Constants;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.utils.JsonHelper;
import com.friday.server.kafka.KafkaProducerManage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: Server Handler
 * @Author: Damon(npf)
 * @Date: 2020-05-10:10:12
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FridayIMServerHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Autowired
    private UidChannelManager uidChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Autowired
    private KafkaProducerManage kafkaProducerManage;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        if (message.getType() == Message.FridayMessage.Type.UpDownMessage) {
            log.info("received msg:{}",JsonHelper.toJsonString(message));
            Message.UpDownMessage upDownMessage = message.getUpDownMessage();
            if (!isMsgClientIsInvalid(channelHandlerContext, upDownMessage)) {
                log.error("client msg is repeat: [{}]", message.getUpDownMessage().getCid());
                return;
            } else {
                saveUserClient(channelHandlerContext, message);
            }

//            if(upDownMessage.getConverType() == Message.ConverType.SINGLE){
//                if(Strings.isNotBlank(upDownMessage.getConverId())){
//
//                }
//            }

            Message.FridayMessage fridayMessage = buildMessage(channelHandlerContext,upDownMessage,100000);
            kafkaProducerManage.send(Constants.KAFKA_TOPIC_SINGLE, String.valueOf(message.getUpDownMessage().getCid()), JsonHelper.toJsonString(fridayMessage));
            log.info("send to kafka success .....");

        } else {
            channelHandlerContext.fireChannelRead(message);
        }

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client[{}] leave server ...", ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    private boolean isMsgClientIsInvalid(ChannelHandlerContext ctx, Message.UpDownMessage message) {
        if (message.getCid() == 0) {
            return false;
        }
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        return conversationRedisServer.isUserCidExit(uid, String.valueOf(message.getCid()));
    }

    private void saveUserClient(ChannelHandlerContext ctx, Message.FridayMessage message) {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        conversationRedisServer.saveUserClientId(uid, String.valueOf(message.getUpDownMessage().getCid()));
    }

    private Message.FridayMessage buildMessage(ChannelHandlerContext ctx, Message.UpDownMessage message, long msgId) {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        Message.MessageContent content = Message.MessageContent.newBuilder()
                .setId(msgId)
                .setType(message.getContent().getType())
                .setUid("123456")
                .setContent(message.getContent().getContent())
                .setTime(System.currentTimeMillis()).build();
        Message.UpDownMessage upDownMessage = Message.UpDownMessage.newBuilder()
                .setCid(message.getCid())
                .setFromUid(message.getFromUid())
                .setContent(content)
                .setConverId(message.getConverId())
                .setConverType(message.getConverType())
                .setGroupId(message.getGroupId()).build();
        return Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.UpDownMessage).setUpDownMessage(upDownMessage).build();
    }
}
