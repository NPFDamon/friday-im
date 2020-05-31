package com.friday.server.kafka.message.processor;

import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserServerRedisService;
import com.friday.common.utils.JsonHelper;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-19:11:59
 */
@Slf4j
public abstract class MessageProcessor{

//    private ServerChannelManager serverChannelManager;
    private UserServerRedisService userServerRedisService;
    private String message;
    private ConversationRedisServer conversationRedisServer;
    private UidChannelManager uidChannelManager;

    public MessageProcessor(UserServerRedisService userServerRedisService, String message
            , ConversationRedisServer conversationRedisServer, UidChannelManager uidChannelManager) {
        this.message = message;
//        this.serverChannelManager = serverChannelManager;
        this.userServerRedisService = userServerRedisService;
        this.conversationRedisServer = conversationRedisServer;
        this.uidChannelManager = uidChannelManager;
    }

    public void init() {
        log.info("get kafka msg:{}", message);
        Message.FridayMessage.Builder builder = Message.FridayMessage.newBuilder();
        JsonHelper.readValue(message, builder);
        Message.UpDownMessage upDownMessage = builder.getUpDownMessage();
        conversationRedisServer.saveMsgToConversation(upDownMessage.getContent(), upDownMessage.getConverId());
        List<String> uids = conversationRedisServer.getUidListByConversationExcludeSender(upDownMessage.getConverId(), upDownMessage.getFromUid());

        if (CollectionUtils.isNotEmpty(uids)) {
            uids.forEach(uid -> {
                List<Channel> channel = uidChannelManager.getChannelById(uid);
                if (CollectionUtils.isNotEmpty(channel)) {
                    channel.forEach(c -> {
                        log.info("channel:[{}]", JsonHelper.toJsonString(channel));
                        Message.UpDownMessage downMessage = Message.UpDownMessage.newBuilder().mergeFrom(upDownMessage).build();
                        Message.FridayMessage fridayMessage = Message.FridayMessage.newBuilder()
                                .setType(Message.FridayMessage.Type.UpDownMessage).setUpDownMessage(downMessage).build();
                        c.writeAndFlush(fridayMessage);
                    });

                }
//                ServerInfo serverInfo = userServerRedisService.getServerInfoByUid(uid);
//
//                if (serverInfo != null) {
//                    Message.UpDownMessage downMessage = Message.UpDownMessage.newBuilder().mergeFrom(upDownMessage).build();
//                    log.info("server info :[{}]", serverInfo.toString());
//                    Channel channel = serverChannelManager.getChannelByServer(serverInfo);
//
//                    if (channel != null) {
//                        log.info("channel :[{}]", channel.toString());
//                        Message.FridayMessage fridayMessage = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.UpDownMessage)
//                                .setUpDownMessage(downMessage).build();
//                        channel.writeAndFlush(fridayMessage);
//                    } else {
//                        log.error("cannot find channel！ server:{}", serverInfo);
//                    }
//                } else {
//                    log.info("uid:{} no server to push down msg:{}.", uid, upDownMessage.getRequestId());
//                }
            });
        }
    }
}
