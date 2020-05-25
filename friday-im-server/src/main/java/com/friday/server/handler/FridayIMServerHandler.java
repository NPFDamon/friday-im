package com.friday.server.handler;

import com.friday.common.bean.resVo.Result;
import com.friday.common.constant.Constants;
import com.friday.common.enums.ResultCode;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.utils.JsonHelper;
import com.friday.common.utils.SnowFlake;
import com.friday.common.utils.UidUtil;
import com.friday.server.kafka.KafkaProducerManage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private SnowFlake snowFlake;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        if (message.getType() == Message.FridayMessage.Type.UpDownMessage) {
            log.info("received msg:{}", JsonHelper.toJsonString(message));
            Message.UpDownMessage upDownMessage = message.getUpDownMessage();
            if (!isMsgClientIsInvalid(channelHandlerContext, upDownMessage)) {
                log.error("client:[{}] msg is repeat", message.getUpDownMessage().getCid());
                sendFailAck(channelHandlerContext,upDownMessage, Message.Code.CLIENT_ID_REPEAT);
                return;
            } else {
                saveUserClient(channelHandlerContext, message);
            }

            String topic;
            String conversationId;
            //单聊处理
            if (upDownMessage.getConverType() == Message.ConverType.SINGLE) {
                if (StringUtils.isNotBlank(upDownMessage.getConverId())) {
                    if (!conversationRedisServer.isSingleConversationIdValid(upDownMessage.getConverId())) {
                        log.error("illegal conversation id !");
                        return;
                    } else {
                        conversationId = upDownMessage.getConverId();
                    }

                } else if (StringUtils.isNotBlank(upDownMessage.getToUid())) {
                    conversationId = conversationRedisServer.newSingleConversationId(upDownMessage.getFromUid(), upDownMessage.getToUid());
                    upDownMessage = upDownMessage.toBuilder().setConverId(conversationId).build();
                }
                topic = Constants.KAFKA_TOPIC_SINGLE;
                //群聊处理
            } else if (upDownMessage.getConverType() == Message.ConverType.GROUP) {

                conversationId = upDownMessage.getConverId();

                if (StringUtils.isNotBlank(conversationId)) {
                    String groupId = conversationRedisServer.getGroupIdByConversationId(conversationId);
                    if (StringUtils.isBlank(groupId)) {
                        log.error("illegal conversation id ！");
                    }
                    upDownMessage = upDownMessage.toBuilder().setGroupId(groupId).build();
                } else if (StringUtils.isNotBlank(upDownMessage.getGroupId())) {
                    conversationId = UidUtil.uuid24ByFactor(upDownMessage.getGroupId());
                    upDownMessage = upDownMessage.toBuilder().setConverId(conversationId).build();

                } else {
                    log.error("conversation id and group id all empty.");
                    return;
                }
                topic = Constants.KAFKA_TOPIC_GROUP;
            } else {
                log.error("illegal conversation type.");
                return;
            }
            Message.FridayMessage fridayMessage = buildMessage(channelHandlerContext, upDownMessage, snowFlake.nextId());
            boolean res = sendToKafka(topic, upDownMessage.getRequestId(), fridayMessage);
            if (res) {
                log.info("send to kafka success .....");
            } else {
                log.info("send kafka fail");
            }

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
        return !conversationRedisServer.isUserCidExit(uid, String.valueOf(message.getCid()));
    }

    private void saveUserClient(ChannelHandlerContext ctx, Message.FridayMessage message) {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        conversationRedisServer.saveUserClientId(uid, String.valueOf(message.getUpDownMessage().getCid()));
    }

    private boolean sendToKafka(String topic, long id, Message.FridayMessage message) {
        String msg = JsonHelper.toJsonString(message);
        if (message == null) {
            return false;
        }
        Result result = kafkaProducerManage.send(topic, String.valueOf(id), msg);
        return result.getCode() == ResultCode.COMMON_SUCCESS.getCode();
    }

    private Message.FridayMessage buildMessage(ChannelHandlerContext ctx, Message.UpDownMessage message, long msgId) {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        Message.MessageContent content = Message.MessageContent.newBuilder()
                .setId(msgId)
                .setType(message.getContent().getType())
                .setUid(uid)
                .setContent(message.getContent().getContent())
                .setTime(System.currentTimeMillis()).build();
        Message.UpDownMessage upDownMessage = Message.UpDownMessage.newBuilder()
                .setCid(message.getCid())
                .setFromUid(message.getFromUid())
                .setToUid(message.getToUid())
                .setContent(content)
                .setConverId(message.getConverId())
                .setConverType(message.getConverType())
                .setGroupId(message.getGroupId()).build();
        return Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.UpDownMessage).setUpDownMessage(upDownMessage).build();
    }
    private void sendFailAck(ChannelHandlerContext ctx, Message.UpDownMessage message, Message.Code code) {
        sendAck(ctx, message, code, 0);
    }

    private void sendAck(ChannelHandlerContext ctx, Message.UpDownMessage message, Message.Code code, long msgId) {
        Message.MessageAck messageAck = Message.MessageAck.newBuilder()
                .setId(msgId)
                .setConverId(message.getConverId())
                .setTargetUid(message.getFromUid())
                .setCid(message.getCid())
                .setCode(code)
                .setTime(System.currentTimeMillis())
                .build();
        Message.FridayMessage ravenMessage = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.MessageAck)
                .setMessageAck(messageAck).build();
        ctx.writeAndFlush(ravenMessage);
    }


}
