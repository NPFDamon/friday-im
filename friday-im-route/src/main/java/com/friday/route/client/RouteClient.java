package com.friday.route.client;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.resVo.Result;
import com.friday.common.enums.ResultCode;
import com.friday.common.protobuf.Message;
import com.friday.common.utils.SnowFlake;
import com.friday.route.client.handle.RouteClientHandler;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-15:15:07
 */
@Component
@Slf4j
public class RouteClient implements Client {
    @Autowired
    private RouteClientHandler routeClientHandler;
    @Autowired
    private SnowFlake snowFlake;

    Channel channel;
    private final AtomicLong atomicLong = new AtomicLong(1);

    public void connect(ServerInfo serverInfo) {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new IdleStateHandler(10, 10, 15))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(Message.FridayMessage.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(routeClientHandler);
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(serverInfo.getIp(), serverInfo.getTcpPort()).sync();
            if (future.isSuccess()) {
                log.info("Friday Netty Client connect server Address[{}],Port[{}] Success ...", serverInfo.getIp(), serverInfo.getTcpPort());
                channel = future.channel();
            }
        } catch (InterruptedException e) {
            log.error("Friday Netty Server connect server Address[{}],Port[{}] fail ...", serverInfo.getIp(), serverInfo.getTcpPort());
            e.printStackTrace();
        }
    }


    @Override
    public void sendMsg(MessageContext messageContext) {
        Message.MessageContent content = Message.MessageContent.newBuilder()
                .setId(snowFlake.nextId())
                .setTime(System.currentTimeMillis())
                .setUid(messageContext.getFromUid())
                .setType(messageContext.getMessageType())
                .setContent(messageContext.getContent()).build();
        Message.UpDownMessage upDownMessage = Message.UpDownMessage.newBuilder()
                .setRequestId(snowFlake.nextId())
                .setCid(atomicLong.incrementAndGet())
                .setFromUid(messageContext.getFromUid())
                .setToUid(messageContext.getToUid())
                .setConverType(messageContext.getConverType())
                .setContent(content).build();
        Message.FridayMessage message = Message.FridayMessage.newBuilder()
                .setType(Message.FridayMessage.Type.UpDownMessage)
                .setUpDownMessage(upDownMessage).build();
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListeners((ChannelFutureListener) channelFuture -> {
            if (future.isSuccess()) {
                log.info("send msg success");
            }
        });
    }

    @Override
    public Result login(UserLoginBeanVO loginBeanVO) {
        Result result = new Result();
        Message.Login login = Message.Login.newBuilder()
                .setToken(loginBeanVO.getToken()).setId(snowFlake.nextId())
                .setUid(loginBeanVO.getUid()).build();
        Message.FridayMessage message = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.Login).setLogin(login).build();
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListeners((ChannelFutureListener) channelFuture -> {
            if (future.isSuccess()) {
                result.setCode(ResultCode.COMMON_SUCCESS.getCode());
            } else {
                result.setCode(ResultCode.COMMON_ERROR.getCode());
            }
        });
        return result;
    }

    @Override
    public void reconnection() {

    }

}
