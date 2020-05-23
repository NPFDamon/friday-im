package com.friday.route.service.impl;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.LoginResVo;
import com.friday.common.bean.resVo.Result;
import com.friday.common.bean.token.Token;
import com.friday.common.constant.Constants;
import com.friday.common.enums.LoginStatusEnum;
import com.friday.common.exception.BizException;
import com.friday.common.netty.ServerChannelManager;
import com.friday.common.netty.UidChannelManager;
import com.friday.common.protobuf.Message;
import com.friday.common.redis.UserInfoRedisService;
import com.friday.common.redis.UserServerRedisService;
import com.friday.common.utils.SnowFlake;
import com.friday.route.cache.ServerCache;
import com.friday.route.client.RouteClient;
import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import com.friday.route.service.AccountService;
import com.friday.route.util.ServerInfoParseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("123456")
    private String uid;
    @Value("test123456")
    private String secret;

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
    private UidChannelManager uidChannelManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SnowFlake snowFlake;

    private Channel channel;

    /**
     * 只获取token server逻辑需转移
     *
     * @param userReqVo
     * @return
     */

    @Override
    public LoginResVo getToken(UserReqVo userReqVo) {
        try {
            LoginResVo resVo = new LoginResVo();
            if (userReqVo.getUid().equals(uid) && userReqVo.getSecret().equals(secret)) {
                resVo.setLoginStatus(LoginStatusEnum.SUCCESS);
            } else {
                resVo.setLoginStatus(LoginStatusEnum.ACCOUNT_NOT_MATCH);
            }
            //获取token
            String token = new Token(uid, secret).getToken(secret);
            resVo.setToken(token);
            //存储token
            stringRedisTemplate.opsForValue().set(token, userReqVo.getUid(), Constants.TOKEN_CACHE_DURATION, TimeUnit.DAYS);
            //获取服务器信息
            List<String> servers = serverCache.getServerList();
            //根据负载均衡策略选取服务器
            ServerInfo serverInfo = serverRouteLoadBalanceHandler.routeServer(ServerInfoParseUtil.getServerInfoList(servers), userReqVo.getUid());
            //保存服务器信息
            userServerRedisService.addUserToServer(userReqVo.getUid(), serverInfo);
            //连接服务器
            channel = client.connect(serverInfo);
            //保存server channel关系
            serverChannelManager.addServerToChannel(serverInfo, channel);

            Message.Login login = Message.Login.newBuilder()
                    .setToken(token).setId(snowFlake.nextId())
                    .setUid(uid).build();
            Message.FridayMessage message = Message.FridayMessage.newBuilder().setType(Message.FridayMessage.Type.Login).setLogin(login).build();
            ChannelFuture future = channel.writeAndFlush(message);
            future.addListeners(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (future.isSuccess()) {
                        log.info("login success");
                    }
                }
            });


            log.info("connect server ip[{}]:port[{}] success", serverInfo.getIp(), serverInfo.getPort());
            return resVo;
        } catch (Exception e) {
            throw new BizException("login error");
        }

    }

    @Override
    public void offLine(Long uid, String token) {
        //todo 下线逻辑
        userInfoRedisService.offLine(token);
    }

    @Override
    public void sendMsg(String token, String msg) {
        Message.MessageContent content = Message.MessageContent.newBuilder()
                .setId(snowFlake.nextId())
                .setTime(System.currentTimeMillis())
                .setUid(String.valueOf(snowFlake.nextId()))
                .setType(Message.MessageType.TEXT)
                .setContent(msg).build();
        Message.UpDownMessage upDownMessage = Message.UpDownMessage.newBuilder()
                .setRequestId(snowFlake.nextId())
                .setCid(Long.parseLong(token))
                .setFromUid(token)
                .setToUid(String.valueOf(snowFlake.nextId()))
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
    }

    @Override
    public Result sendLogin(String uid, String token) {

//        ChannelFuture future = channel.writeAndFlush(login);
//        future.addListeners(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//
//                log.info(String.valueOf(channelFuture.isSuccess()));
//            }
//        });
//        if (future.isSuccess()) {
//            return Result.success(ResultCode.COMMON_SUCCESS);
//        } else {
//            return Result.failure(ResultCode.COMMON_ERROR);
//        }
        return null;
    }
}
