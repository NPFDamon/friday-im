package com.friday.server.handler;

import com.friday.server.protobuf.FridayMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: Group Handler
 * @Author: Damon(npf)
 * @Date: 2020-05-10:11:50
 */
@Slf4j
@Component
public class FridayIMServerGroupHandler extends SimpleChannelInboundHandler<FridayMessage.Message> {

    private static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage.Message s) throws Exception {
        log.info("Receive User[{}] msg[{}]", channelHandlerContext.channel().id(), s.getContent().toString());
        for (Channel channel : GROUP) {
            channel.writeAndFlush(s);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        GROUP.add(ctx.channel());
        log.info("User[{}] join the chat ...",ctx.channel().id());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("User[{}] leave the chat ... ",ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Chat is error ...");
        super.exceptionCaught(ctx, cause);
    }
}
