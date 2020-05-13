package com.friday.server.server;

import com.friday.server.handler.FridayIMServerHandler;
import com.friday.server.protobuf.FridayMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
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
                        socketChannel.pipeline()
                                //10秒没有向客户端发送消息就发送心跳
                                .addLast(new IdleStateHandler(10, 0, 0))
                                //google protobuf 编解码
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(FridayMessage.Message.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new FridayIMServerHandler());
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
