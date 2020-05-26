package com.friday.server.handler;

import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.utils.SnowFlake;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-24:10:04
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FridayIMMessageRouteHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {


    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    private UidChannelManager uidChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("server connected remote address:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if ("Connection reset by peer".equals(cause.getMessage())) {
            return;
        }
        log.error(cause.getMessage(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("server disconnect remote address:{}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage message) throws Exception {
        NettyAttrUtil.updateReadTime(channelHandlerContext.channel(), System.currentTimeMillis());
        if (message.getType() == Message.FridayMessage.Type.HeartBeat) {
            Message.HeartBeat heartBeat = message.getHeartBeat();
            log.debug("received heart beat msg:{}", heartBeat);
            if (heartBeat.getHeartBeatType() == Message.HeartBeatType.PING) {
                Message.HeartBeat pong = Message.HeartBeat.newBuilder()
                        .setHeartBeatType(Message.HeartBeatType.PONG)
                        .setId(heartBeat.getId()).build();
                Message.FridayMessage msg = Message.FridayMessage.newBuilder()
                        .setType(Message.FridayMessage.Type.HeartBeat)
                        .setHeartBeat(pong).build();
                channelHandlerContext.writeAndFlush(msg);
            }
        } else if (message.getType() == Message.FridayMessage.Type.UpDownMessage) {
            Message.UpDownMessage downMessage = message.getUpDownMessage();
            log.debug("received down message:{}", downMessage);
            conversationRedisServer.saveWaitUserAckMsg(downMessage.getToUid(), downMessage.getConverId(), downMessage.getRequestId());
            List<Channel> channels = uidChannelManager.getChannelById(downMessage.getToUid());
            if (CollectionUtils.isNotEmpty(channels)) {
                Message.FridayMessage fridayMessage = Message.FridayMessage.newBuilder()
                        .setType(Message.FridayMessage.Type.UpDownMessage)
                        .setUpDownMessage(downMessage).build();
                for (Channel channel : channels) {
                    channel.writeAndFlush(fridayMessage).addListeners(future -> {
                        log.info("send msg to channel:{}", channel);
                        if (!future.isSuccess()) {
                            log.info("push msg to uid:{} fail", downMessage.getToUid());
                            channel.close();
                        }
                    });
                }
            } else {
                log.error("uid:{} is not login in", downMessage.getToUid());
            }

        } else {
            channelHandlerContext.fireChannelRead(message);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                Long lastReadTime = NettyAttrUtil.getReadTime(ctx.channel());
                if (null != lastReadTime && System.currentTimeMillis() - lastReadTime > 30000) {
                    log.info("server:{} last read time more than 30 seconds",
                            ctx.channel().remoteAddress());
                    ctx.close();
                    return;
                }
                Message.HeartBeat heartBeat = Message.HeartBeat.newBuilder()
                        .setId(snowFlake.nextId())
                        .setHeartBeatType(Message.HeartBeatType.PING)
                        .build();
                Message.FridayMessage message = Message.FridayMessage.newBuilder()
                        .setHeartBeat(heartBeat)
                        .setType(Message.FridayMessage.Type.HeartBeat).build();
                ctx.writeAndFlush(message).addListeners(future -> {
                    if (!future.isSuccess()) {
                        log.info("server:{} off line", ctx.channel().remoteAddress());
                        ctx.close();
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
