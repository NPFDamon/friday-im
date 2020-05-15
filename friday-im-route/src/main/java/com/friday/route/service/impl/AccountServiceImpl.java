package com.friday.route.service.impl;

import com.friday.route.cache.ServerCache;
import com.friday.route.client.RouteClient;
import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import com.friday.route.redis.UserInfoRedisService;
import com.friday.route.redis.UserServerRedisService;
import com.friday.route.service.AccountService;
import com.friday.route.util.ServerInfoParseUtil;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.bean.reqVo.UserReqVo;
import com.friday.server.bean.resVo.LoginResVo;
import com.friday.server.bean.token.Token;
import com.friday.server.enums.LoginStatusEnum;
import com.friday.server.netty.ServerChannelManager;
import com.friday.server.netty.UidChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * 只获取token server逻辑需转移
     * @param userReqVo
     * @return
     */

    @Override
    public LoginResVo login(UserReqVo userReqVo) {
        LoginResVo resVo = new LoginResVo();
        if (userReqVo.getUid().equals(uid) && userReqVo.getSecret().equals(secret)) {
            resVo.setLoginStatus(LoginStatusEnum.SUCCESS);
        } else {
            resVo.setLoginStatus(LoginStatusEnum.ACCOUNT_NOT_MATCH);
        }
        resVo.setToken(new Token(uid, secret).getToken(secret));
        userInfoRedisService.storeUserLoginInfo(resVo);
        //获取服务器信息
        List<String> servers = serverCache.getServerList();
        //根据负载均衡策略选取服务器
        ServerInfo serverInfo = serverRouteLoadBalanceHandler.routeServer(ServerInfoParseUtil.getServerInfoList(servers), userReqVo.getUid());
        //保存服务器信息
        userServerRedisService.addUserToServer(userReqVo.getUid(), serverInfo);
        //连接server
        Channel channel = client.connect(serverInfo);
        if (channel != null) {
            //保存channel和server关系
            serverChannelManager.addServerToChannel(serverInfo, channel);
            //保存channel和UID关系
            uidChannelManager.addUserToChannel(userReqVo.getUid(), channel);
        } else {

        }
        log.info("connect server ip[{}]:port[{}] success", serverInfo.getIp(), serverInfo.getIp());
        return resVo;
    }

    @Override
    public void offLine(Long uid, String token) {
        //todo 下线逻辑
        userInfoRedisService.offLine(token);
    }
}
