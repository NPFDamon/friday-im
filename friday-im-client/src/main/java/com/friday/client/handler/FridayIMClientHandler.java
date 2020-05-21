package com.friday.client.handler;

import com.friday.server.netty.NettyAttrUtil;
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
public class FridayIMClientHandler extends SimpleChannelInboundHandler<FridayMessage.Message> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof FridayMessage.Message) {
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
                FridayMessage.Message heartBean = FridayMessage.Message.newBuilder()
                        .setConverType(FridayMessage.ConverType.PING).build();
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage.Message message) throws Exception {
        if (message.getConverType().equals(FridayMessage.ConverType.PING)) {
            log.info("Client Received heart bean PONG msg ...");
            NettyAttrUtil.updateReadTime(channelHandlerContext.channel(), System.currentTimeMillis());
            FridayMessage.Message heartBean = FridayMessage.Message.newBuilder()
                    .setConverType(FridayMessage.ConverType.PING).build();
            channelHandlerContext.writeAndFlush(heartBean).addListeners((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.info("IO Error, close channel ...");
                    channelFuture.channel().close();
                }
            });
        }

        log.info("MsgType[{}],MsgContent[{}]", message.getConverType().toString(), message.getContent().toString());
    }
}
