package com.friday.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class FridayIMClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        log.info("Msg[{}]", s);
    }
}
