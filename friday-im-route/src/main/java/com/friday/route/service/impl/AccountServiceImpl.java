package com.friday.route.service.impl;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.reqVo.GroupOut;
import com.friday.common.bean.reqVo.GroupParams;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.LoginResVo;
import com.friday.common.bean.token.Token;
import com.friday.common.constant.Constants;
import com.friday.common.enums.LoginStatusEnum;
import com.friday.common.exception.BizException;
import com.friday.common.netty.ServerChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.ConversationRedisServer;
import com.friday.common.redis.UserInfoRedisService;
import com.friday.common.redis.UserServerRedisService;
import com.friday.common.utils.JsonHelper;
import com.friday.common.utils.ServerInfoParseUtil;
import com.friday.common.utils.SnowFlake;
import com.friday.common.utils.UidUtil;
import com.friday.route.cache.ServerCache;
import com.friday.route.client.RouteClient;
import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import com.friday.route.service.AccountService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C),Damon
 *
 * @Description: impl
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:37
 */
@Component
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserInfoRedisService userInfoRedisService;

    @Autowired
    private ServerCache serverCache;

    @Autowired
    private ServerRouteLoadBalanceHandler serverRouteLoadBalanceHandler;

    @Autowired
    private UserServerRedisService userServerRedisService;

    @Autowired
    private RouteClient client;

    @Autowired
    private ServerChannelManager serverChannelManager;

    @Autowired
    private ConversationRedisServer conversationRedisServer;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SnowFlake snowFlake;

    /**
     * 只获取token server逻辑需转移
     *
     * @param userReqVo
     * @return
     */

    @Override
    public LoginResVo getToken(UserReqVo userReqVo) {
        LoginResVo resVo = new LoginResVo();
        try {
            if (userServerRedisService.getServerInfoByUid(userReqVo.getUid()) == null) {

                //获取token
                String token = new Token(userReqVo.getUid(), userReqVo.getSecret()).getToken(userReqVo.getSecret());
                resVo.setToken(token);
                //存储token
                stringRedisTemplate.opsForValue().set(token, userReqVo.getUid(), Constants.TOKEN_CACHE_DURATION, TimeUnit.DAYS);
                //获取服务器信息
                List<String> servers = serverCache.getServerList();
                //根据负载均衡策略选取服务器
                ServerInfo serverInfo = serverRouteLoadBalanceHandler.routeServer(ServerInfoParseUtil.getServerInfoList(servers), userReqVo.getUid());

                Channel channel = serverChannelManager.getChannelByServer(serverInfo);
                if (channel == null) {
                    //连接服务器
                    channel = client.connect(serverInfo);
                    log.info("uid:[{}] login,channel[{}] ", userReqVo.getUid(), channel);
                    //保存server channel关系
                    serverChannelManager.addServerToChannel(serverInfo, channel);

                }
                userServerRedisService.addUserToServer(userReqVo.getUid(), serverInfo);
                Message.Login login = Message.Login.newBuilder()
                        .setToken(token).setId(snowFlake.nextId())
                        .setUid(userReqVo.getUid()).build();
                Message.FridayMessage message = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.Login).setLogin(login).build();
                ChannelFuture future = channel.writeAndFlush(message);
                future.addListeners((ChannelFutureListener) channelFuture -> {
                    if (future.isSuccess()) {
                        log.info("connect server ip[{}]:port[{}] success", serverInfo.getIp(), serverInfo.getHttpPort());
                        resVo.setLoginStatus(LoginStatusEnum.SUCCESS);
                    } else {
                        resVo.setLoginStatus(LoginStatusEnum.SERVER_NOT_AVAILABLE);
                    }
                });
            } else {
                log.error("user:{} is login already", userReqVo.getUid());
                resVo.setLoginStatus(LoginStatusEnum.REPEAT_LOGIN);
            }
        } catch (Exception e) {
            throw new BizException("login error");
        }
        return resVo;
    }

    @Override
    public void offLine(Long uid, String token) {
        //todo 下线逻辑
        userInfoRedisService.offLine(token);
    }

    @Override
    public void sendMsg(String uid, String msg, String toUid) {
        ServerInfo serverInfo = userServerRedisService.getServerInfoByUid(uid);
        if (serverInfo != null) {
            Channel channel = serverChannelManager.getChannelByServer(serverInfo);
            if (channel != null) {
                Message.MessageContent content = Message.MessageContent.newBuilder()
                        .setId(snowFlake.nextId())
                        .setTime(System.currentTimeMillis())
                        .setUid(uid)
                        .setType(Message.MessageType.TEXT)
                        .setContent(msg).build();
                Message.UpDownMessage upDownMessage = Message.UpDownMessage.newBuilder()
                        .setRequestId(snowFlake.nextId())
                        .setCid(Long.parseLong(uid))
                        .setFromUid(uid)
                        .setToUid(toUid)
                        .setConverType(Message.ConverType.SINGLE)
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
            } else {
                log.error("client is not connect sever:{}", JsonHelper.toJsonString(serverInfo));
            }
        } else {
            log.error("user:{} is not login !", uid);
        }
    }

    @Override
    public GroupOut createGroup(GroupParams params) {
        String groupId = UidUtil.uuid();
        String converId = conversationRedisServer.newGroupConversationId(groupId, params.getMembers());
        GroupOut out = new GroupOut();
        out.setConverId(converId);
        out.setGroupId(groupId);
        return out;
    }
}
