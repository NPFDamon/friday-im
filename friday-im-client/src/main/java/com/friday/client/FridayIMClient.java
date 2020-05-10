package com.friday.client;

import com.friday.handler.FridayIMClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: Friday IM Client
 * @Author: Damon(npf)
 * @Date: 2020-05-10:11:09
 */
@Slf4j
@Component
public class FridayIMClient {

    @Value("${netty.server.port}")
    private int port;

    @Value("${netty.server.address}")
    private String address;

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decode", new StringDecoder());
                        pipeline.addLast("encode", new StringEncoder());
                        pipeline.addLast(new FridayIMClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(address, port).sync();
            future.channel().writeAndFlush("Hello World!");
            future.channel().closeFuture().sync();
            if(future.isSuccess()){
                log.info("Friday Netty Client Start Success With Address[{}],Port[{}] ...", address, port);
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Friday Netty Server Start  With Address[{}],Port[{}] ...", address, port);
            eventLoopGroup.shutdownGracefully();
        }
    }
}
