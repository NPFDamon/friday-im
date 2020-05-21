package com.friday.client.handler;

import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.protobuf.Message;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: Client Handler
 * @Author: Damon(npf)
 * @Date: 2020-05-10:11:23
 */
@Component
@Slf4j
public class FridayIMClientHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof Message.FridayMessage) {
            log.info("定时检查server存活 ... ");
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                Long lastReadTime = NettyAttrUtil.getReadTime(ctx.channel());
                if (lastReadTime == null) {
                    log.info("last read time is null");
                    ctx.close();
                }
                if (System.currentTimeMillis() - lastReadTime > 30000) {
                    log.info("Server heat bean more than 30 seconds");
                    ctx.close();
                }
                Message.FridayMessage heartBean = Message.FridayMessage.newBuilder()
                        .setHeartBeat(Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PONG).build()).build();
                ctx.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        log.info("IO Error, close channel ...");
                        channelFuture.channel().close();
                    }
                });
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyAttrUtil.updateReadTime(ctx.channel(), System.currentTimeMillis());
        log.info("Friday IM Server connect success ...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Friday IM Server connect interrupt ...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        if (message.getHeartBeat().equals(Message.HeartBeatType.PING)) {
            log.info("Client Received heart bean PONG msg ...");
            NettyAttrUtil.updateReadTime(channelHandlerContext.channel(), System.currentTimeMillis());
            Message.FridayMessage heartBean = Message.FridayMessage.newBuilder()
                    .setHeartBeat(Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PONG).build()).build();
            channelHandlerContext.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.info("IO Error, close channel ...");
                    channelFuture.channel().close();
                }
            });
        }
    }
}
