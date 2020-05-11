package com.friday.handler;

import com.friday.protobuf.FridayMessage;
import com.friday.utils.SpringBeanFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

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


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(idleStateEvent.state() == IdleState.READER_IDLE){
                log.info("定时检查客户端存活 ... ");
            }

        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FridayMessage.Message message) throws Exception {
        log.info("Received msg[{}]", message.getContent().toString());
        if (message.getConverType().equals(FridayMessage.ConverType.LOGIN)) {
            log.info("login ...");
        }

        if (message.getConverType().equals(FridayMessage.ConverType.PING)) {
            FridayMessage.Message heartBean = SpringBeanFactory.getBean(FridayMessage.Message.class, "heartBean");
            channelHandlerContext.writeAndFlush(heartBean).addListeners(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        log.info("IO Error, close channel ...");
                        channelFuture.channel().close();
                    }
                }
            });
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
