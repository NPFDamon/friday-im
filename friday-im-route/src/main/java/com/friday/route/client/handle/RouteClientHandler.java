package com.friday.route.client.handle;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.netty.ServerChannelManager;
import com.friday.common.protobuf.Message;
import io.netty.channel.ChannelFutureListener;
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
 * @Date: 2020-05-15:15:09
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class RouteClientHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {
    @Autowired
    private ServerChannelManager serverChannelManager;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        if (message.getUpDownMessage().getConverType().equals(Message.HeartBeatType.PONG)) {
            log.info("Client Received heart bean PONG msg ...");
            //返回心跳回应
            Message.HeartBeat heartBeat = Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PING).build();
            Message.FridayMessage heartBean = Message.FridayMessage.newBuilder().setHeartBeat(heartBeat).build();
            channelHandlerContext.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.info("IO Error, close channel ...");
                    channelFuture.channel().close();
                }
            });
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Friday IM Route server connect address[{}]", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Friday IM route server disconnected from address:[{}]", ctx.channel().remoteAddress());
        ServerInfo serverInfo = serverChannelManager.getServerByChannel(ctx.channel());
        serverChannelManager.removeServer(serverInfo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
