package com.friday.server.handler;

import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.utils.JsonHelper;
import com.friday.server.kafka.KafkaProducerManage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: login
 * @Author: Damon(npf)
 * @Date: 2020-05-21:10:47
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class FridayIMServerAuthHandler extends SimpleChannelInboundHandler<Message.FridayMessage> {

    @Autowired
    private UidChannelManager uidChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Autowired
    private KafkaProducerManage kafkaProducerManage;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage fridayMessage) throws Exception {
        if(fridayMessage.getType() == Message.FridayMessage.Type.Login){
            Message.Login login = fridayMessage.getLogin();
            log.info("login msg:[{}]", JsonHelper.toJsonString(login));
            String token = login.getToken();

        }

    }

    private boolean verifyToken(String token){
        return true;
    }
}
