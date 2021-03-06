package com.friday.route.client.handle;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.netty.ServerChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.utils.JsonHelper;
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
@Slf4j
@ChannelHandler.Sharable
@Component
public class RouteClientHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Autowired
    private ServerChannelManager serverChannelManager;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        if (message.getHeartBeat().getHeartBeatType() == Message.HeartBeatType.PING) {
            log.info("Client Received heart bean PING msg ...");
            //返回心跳回应
            Message.HeartBeat heartBeat = Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PONG).build();
            Message.FridayMessage heartBean = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.HeartBeat).setHeartBeat(heartBeat).build();
            channelHandlerContext.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.info("IO Error, close channel ...");
                    channelFuture.channel().close();
                }
            });
        } else if (message.getType() == Message.FridayMessage.Type.UpDownMessage) {
            Message.UpDownMessage downMessage = message.getUpDownMessage();
            log.info("client received msg:{}", JsonHelper.toJsonString(downMessage));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Friday IM Route server connect address[{}]", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerInfo serverInfo = serverChannelManager.getServerByChannel(ctx.channel());
        serverChannelManager.removeServer(serverInfo);
        log.info("Friday IM route server disconnected from address:[{}]", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
