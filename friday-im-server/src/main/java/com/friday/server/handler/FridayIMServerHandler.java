package com.friday.server.handler;

import com.friday.common.constant.Constants;
import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.utils.JsonHelper;
import com.friday.server.kafka.KafkaProducerManage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Copyright (C),Damon
 *
 * @Description: Server Handler
 * @Author: Damon(npf)
 * @Date: 2020-05-10:10:12
 */
@Slf4j
@ChannelHandler.Sharable
public class FridayIMServerHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Autowired
    private UidChannelManager uidChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Autowired
    private KafkaProducerManage kafkaProducerManage;


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("定时检查客户端存活 ... ");
                Long lastReadTime = NettyAttrUtil.getReadTime(ctx.channel());
                if (lastReadTime == null) {
                    log.info("last read time is null");
                    ctx.close();
                }
                if (System.currentTimeMillis() - lastReadTime > 30000) {
                    log.info("Client heat bean more than 30 seconds");
                    ctx.close();
                }
                Message.HeartBeat heartBeat = Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PONG).build();
                Message.FridayMessage heartBean = Message.FridayMessage.newBuilder().setHeartBeat(heartBeat).build();
                ctx.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        log.info("IO Error, close channel ...");
                        channelFuture.channel().close();
                    }
                });
                log.info("Reply PONG heat bean info success ...");
            }
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        NettyAttrUtil.updateReadTime(ctx.channel(), System.currentTimeMillis());
        log.info("Client[{}] join server", ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        log.info("Received msg[{}]", message.getUpDownMessage().getContent().getContent());
        if (message.getUpDownMessage().getConverType().equals(Message.ConverType.SINGLE)) {
            if (isMsgClientIsInvalid(channelHandlerContext, message)) {
                log.error("client msg is repeat: [{}]", message.getUpDownMessage().getCid());
                return;
            }else {
                saveUserClient(channelHandlerContext,message);
            }
            kafkaProducerManage.send(Constants.KAFKA_TOPIC_SINGLE,String.valueOf(message.getUpDownMessage().getCid()), JsonHelper.toJsonString(message));
            log.info("send to kafka success .....");
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

    private boolean isMsgClientIsInvalid(ChannelHandlerContext ctx, Message.FridayMessage message) {
        if (message.getUpDownMessage().getCid() == 0) {
            return false;
        }
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        return conversationRedisServer.isUserCidExit(uid, String.valueOf(message.getUpDownMessage().getCid()));
    }

    private void saveUserClient(ChannelHandlerContext ctx, Message.FridayMessage message) {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        conversationRedisServer.saveUserClientId(uid, String.valueOf(message.getUpDownMessage().getCid()));
    }

//    private FridayMessage buildMessage(ChannelHandlerContext ctx, FridayMessage.Message message){
//        String uid = uidChannelManager.getIdByChannel(ctx.channel());
//        FridayMessage.MessageContent content = FridayMessage.MessageContent.newBuilder()
//                .setId(11111L)
//                .setUid(uid)
//                .setType(FridayMessage.MessageType.TEXT)
//                .setContent(message.getContent().getContent())
//                .build();
//        return FridayMessage.Message.newBuilder().setCid();
//    }
}
