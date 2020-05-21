package com.friday.route.message.processor;

import com.friday.route.redis.UserServerRedisService;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.netty.ServerChannelManager;
import com.friday.server.protobuf.Message;
import com.friday.server.utils.JsonHelper;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:11:59
 */
@Slf4j
public class SingleMessageProcessor implements Runnable {

    private ServerChannelManager serverChannelManager;

    private UserServerRedisService userServerRedisService;

    private String message;

    public SingleMessageProcessor(ServerChannelManager serverChannelManager, UserServerRedisService userServerRedisService, String message) {
        this.message = message;
        this.serverChannelManager = serverChannelManager;
        this.userServerRedisService = userServerRedisService;
    }


    @Override
    public void run() {
        log.info("get kafka msg:{}", message);
        Message.FridayMessage.Builder builder = Message.FridayMessage.newBuilder();
        JsonHelper.readValue(message, builder);
        Message.UpDownMessage upDownMessage = builder.getUpDownMessage();
        String uid = builder.getUpDownMessage().getFromUid();
        ServerInfo serverInfo = userServerRedisService.getServerInfoByUid(uid);
        if (serverInfo != null) {
            log.info("server info :[{}]", serverInfo.toString());
            Channel channel = serverChannelManager.getChannelByServer(serverInfo);
            if (channel != null) {
                log.info("channel :[{}]", channel.toString());
                channel.writeAndFlush(upDownMessage);
            } else {
                log.error("cannot find channel！ server:{}", serverInfo);
            }
        } else {
            log.info("uid:{} no server to push down msg:{}.", uid,upDownMessage.getRequestId());
        }
    }
}
