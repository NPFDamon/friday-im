package com.friday.server.handler;

import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-21:15:38
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class FridayIMServerHeartBeanHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Autowired
    private UidChannelManager uidChannelManager;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("check client is alive ... ");
                String uid = uidChannelManager.getIdByChannel(ctx.channel());
                Long lastReadTime = NettyAttrUtil.getReadTime(ctx.channel());
                if (lastReadTime == null || uid == null) {
                    log.info("last read time is null or uid is null");
                    ctx.close();
                    return;
                }
                if (System.currentTimeMillis() - lastReadTime > 30000) {
                    log.info("uid:[{}] last read more than 30 seconds", uid);
                    ctx.close();
                    return;
                }
                Message.HeartBeat heartBeat = Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PING).build();
                Message.FridayMessage heartBean = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.HeartBeat).setHeartBeat(heartBeat).build();
                ctx.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        log.info("uid:[{}] off line", uid);
                        channelFuture.channel().close();
                    }
                });
                log.info("Send PING heat bean info success ...");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage fridayMessage) throws Exception {
        if (fridayMessage.getType() == Message.FridayMessage.Type.HeartBeat) {
            Message.HeartBeat beat = fridayMessage.getHeartBeat();
            log.info("receive heart beat:{}", beat.getHeartBeatType());
            if (beat.getHeartBeatType() == Message.HeartBeatType.PING) {
                Message.HeartBeat heartBeat = Message.HeartBeat.newBuilder().setHeartBeatType(Message.HeartBeatType.PONG).build();
                Message.FridayMessage message = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.HeartBeat).setHeartBeat(heartBeat).build();
                channelHandlerContext.writeAndFlush(message);
            }

        } else {
            channelHandlerContext.fireChannelRead(fridayMessage);
        }
    }
}
