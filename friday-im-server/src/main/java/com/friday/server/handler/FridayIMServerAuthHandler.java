package com.friday.server.handler;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.UserServerRedisService;
import com.friday.common.utils.JsonHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private UserServerRedisService userServerRedisService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message.FridayMessage fridayMessage) throws Exception {
        NettyAttrUtil.updateReadTime(channelHandlerContext.channel(), System.currentTimeMillis());
        if (fridayMessage.getType() == Message.FridayMessage.Type.Login) {
            Message.Login login = fridayMessage.getLogin();
            log.info("login msg:[{}]", JsonHelper.toJsonString(login));
            String token = login.getToken();
            if (!verifyToken(token)) {
                Message.LoginAck ack = Message.LoginAck.newBuilder()
                        .setId(login.getId())
                        .setCode(Message.Code.TOKEN_INVALID)
                        .setTime(System.currentTimeMillis()).build();
                channelHandlerContext.writeAndFlush(ack);
            }
            uidChannelManager.addUserToChannel(login.getUid(), channelHandlerContext.channel());
            sendLoginAck(channelHandlerContext, login.getId(), Message.Code.SUCCESS);
        } else {
            channelHandlerContext.fireChannelRead(fridayMessage);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String uid = uidChannelManager.getIdByChannel(ctx.channel());
        if (null != uid) {
            log.info("client disconnected uid:[{}]", uid);
            uidChannelManager.removeChannel(ctx.channel());
            if (CollectionUtils.isEmpty(uidChannelManager.getChannelById(uid))) {
                // todo
                userServerRedisService.removeUserFromServer(uid, new ServerInfo());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    private boolean verifyToken(String token) {
        return redisTemplate.hasKey(token);
    }

    private void sendLoginAck(ChannelHandlerContext context, long id, Message.Code code) {
        Message.LoginAck loginAck = Message.LoginAck.newBuilder()
                .setId(id)
                .setCode(code)
                .setTime(System.currentTimeMillis())
                .build();
        Message.FridayMessage fridayMessage = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.LoginAck)
                .setLoginAck(loginAck).build();
        context.writeAndFlush(fridayMessage);
    }
}
