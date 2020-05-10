package com.friday.server;

import com.friday.handler.FridayIMServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Copyright (C),Damon
 *
 * @Description: Im Netty Server
 * @Author: Damon(npf)
 * @Date: 2020-05-10:10:04
 */
@Slf4j
@Component
public class FridayIMServer {

    private final EventLoopGroup boot = new NioEventLoopGroup();

    private final EventLoopGroup work = new NioEventLoopGroup();

    @Value("${netty.server.port}")
    private int port;

    @Value("${netty.server.address}")
    private String address;

    /***
     * Server 启动方法
     */
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boot, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(address, port))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decode", new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast("encode", new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new FridayIMServerHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            if (channelFuture.isSuccess()) {
                log.info("Friday Netty Server Start Success With Address[{}],Port[{}] ...", address, port);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Friday Netty Server Start  With Address[{}],Port[{}] ...", address, port);
            destroy();
        }
    }

    /**
     * server销毁
     */
    public void destroy() {
        boot.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        log.info("Friday Netty Server destroy success ...");
    }


}
