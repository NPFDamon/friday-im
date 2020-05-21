package com.friday.route.client;

import com.friday.route.client.handle.RouteClientHandler;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.exception.BizException;
import com.friday.server.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-15:15:07
 */
@Component
@Slf4j
public class RouteClient {

    public Channel connect(ServerInfo serverInfo) {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new IdleStateHandler(10, 0, 0))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(Message.FridayMessage.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new RouteClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(serverInfo.getIp(), serverInfo.getPort()).sync();
            if (future.isSuccess()) {
                log.info("Friday Netty Client connect server Address[{}],Port[{}] ...", serverInfo.getIp(), serverInfo.getPort() + "Success");
                return future.channel();
            }
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Friday Netty Server connect server Address[{}],Port[{}] ...", serverInfo.getIp(), serverInfo.getPort() + "fail");
            throw new BizException("");
        }
    }
}
