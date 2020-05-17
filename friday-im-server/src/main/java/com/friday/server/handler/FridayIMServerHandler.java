package com.friday.server.handler;

import com.friday.server.netty.NettyAttrUtil;
import com.friday.server.netty.UidChannelManager;
import com.friday.server.protobuf.FridayMessage;
import com.friday.server.redis.ConversationRedisServer;
import com.friday.server.utils.SpringBeanFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
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
public class FridayIMServerHandler extends SimpleChannelInboundHandler<FridayMessage.Message> {

    @Autowired
    private UidChannelManager uidChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;


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
                FridayMessage.Message heartBean = FridayMessage.Message.newBuilder().setConverType(FridayMessage.ConverType.PING).build();
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage.Message message) throws Exception {
        log.info("Received msg[{}]", message.getContent().toString());
        if (message.getConverType().equals(FridayMessage.ConverType.MSG)) {
            if (isMsgClientIsInvalid(channelHandlerContext, message)) {
                log.error("client msg is repeat: [{}]", message.getCid());
                return;
            }else {
                saveUserClient(channelHandlerContext,message);
            }


        }
        if (message.getConverType().equals(FridayMessage.ConverType.LOGIN)) {
            //todo 登录逻辑放到此处
            log.info("login ...");
        }

        if (message.getConverType().equals(FridayMessage.ConverType.PING)) {
            log.info("Server Received client heat bean PING message ... ");
            //心跳时间更新
            NettyAttrUtil.updateReadTime(channelHandlerContext.channel(), System.currentTimeMillis());
            FridayMessage.Message heartBean = FridayMessage.Message.newBuilder().setConverType(FridayMessage.ConverType.PING).build();
            channelHandlerContext.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.info("IO Error, close channel ...");
                    channelFuture.channel().close();
                }
            });

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

    private boolean isMsgClientIsInvalid(ChannelHandlerContext ctx, FridayMessage.Message message) {
        if (message.getCid() == 0) {
            return false;
        }
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        return conversationRedisServer.isUserCidExit(uid, String.valueOf(message.getCid()));
    }

    private void saveUserClient(ChannelHandlerContext ctx, FridayMessage.Message clientId) {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        conversationRedisServer.saveUserClientId(uid, String.valueOf(clientId.getCid()));
    }

    private FridayMessage buildMessage(ChannelHandlerContext ctx, FridayMessage.Message message){
//        String uid = uidChannelManager.getIdByChannel(ctx.channel());
//        FridayMessage.MessageContent content = FridayMessage.MessageContent.newBuilder()
//                .setId("11111")
//                .setUid(uid)
//                .setType(FridayMessage.MessageType.TEXT)
//                .setContent(message.g)
        return null;
    }
}
