package com.friday.client.client;

import com.friday.client.handler.FridayIMClientHandler;
import com.friday.server.protobuf.FridayMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Scanner;

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
                        socketChannel.pipeline()
                                .addLast(new IdleStateHandler(10, 0, 0))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(FridayMessage.Message.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new FridayIMClientHandler());
                    }
                });
        try {
//            ChannelFuture future = bootstrap.connect(address, port).sync();
//            Scanner scanner = new Scanner(System.in);
//            while (true){
//                String msg = scanner.nextLine();
//                if("exit".equals(msg)){
//                    break;
//                }
//                future.channel().writeAndFlush(future.channel().id() + ": " + msg);
//            }
//            future.channel().closeFuture().sync();
            ChannelFuture future = bootstrap.connect(address, port).sync();
            if (future.isSuccess()) {
                log.info("Friday Netty Client Start Success With Address[{}],Port[{}] ...", address, port);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Friday Netty Server Start  With Address[{}],Port[{}] ...", address, port);
            eventLoopGroup.shutdownGracefully();
        }
    }
}
