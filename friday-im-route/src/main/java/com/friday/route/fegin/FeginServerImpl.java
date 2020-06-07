package com.friday.route.fegin;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.bean.reqVo.MessageContext;
import com.friday.common.bean.reqVo.UserLoginBeanVO;
import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.bean.resVo.Result;
import com.friday.common.bean.token.Token;
import com.friday.common.constant.Constants;
import com.friday.common.enums.ResultCode;
import com.friday.common.exception.TokenException;
import com.friday.common.utils.ServerInfoParseUtil;
import com.friday.route.cache.ServerCache;
import com.friday.route.client.Client;
import com.friday.route.fegin.interfaces.FeginService;
import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-02:12:22
 */
@RestController
public class FeginServerImpl implements FeginService {
    @Autowired
    private Client client;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ServerRouteLoadBalanceHandler serverRouteLoadBalanceHandler;
    @Autowired
    private ServerCache serverCache;

    @Override
    public Result registry(UserReqVo reqVo) {
        return null;
    }

    @Override
    public Result getToken(UserReqVo reqVo) {
        try {
            String token = new Token(reqVo.getUid(), reqVo.getSecret()).getToken(reqVo.getSecret());
            String key = reqVo.getUid() + Constants.DEFAULT_SEPARATES + reqVo.getSecret();
            stringRedisTemplate.opsForValue().set(token, key, Constants.TOKEN_CACHE_DURATION, TimeUnit.DAYS);
            return Result.success(token);
        } catch (TokenException e) {
            return Result.failure(ResultCode.APP_ERROR_TOKEN_CREATE_ERROR);
        }
    }

    @Override
    public Result getServer(String token) {
        if(!stringRedisTemplate.hasKey(token)){
            return Result.failure(ResultCode.APP_ERROR_TOKEN_INVALID);
        }
        String tokenStr = stringRedisTemplate.opsForValue().get(token);
        String uid = tokenStr.split(Constants.DEFAULT_SEPARATES)[0];
        //获取服务器信息
        List<String> servers = serverCache.getServerList();
        if(CollectionUtils.isNotEmpty(servers)){
            //根据负载均衡策略选取服务器
            ServerInfo serverInfo = serverRouteLoadBalanceHandler.routeServer(ServerInfoParseUtil.getServerInfoList(servers), uid);
            return Result.success(serverInfo);
        }else {
            return Result.failure(ResultCode.COMMON_SERVER_NOT_AVAILABLE);
        }
    }

    @Override
    public Result sendMsg(MessageContext messageContext) {
        client.sendMsg(messageContext);
        return Result.success();
    }


    @Override
    public Result login(UserLoginBeanVO vo) {
        if(!stringRedisTemplate.hasKey(vo.getToken())){
            return Result.failure(ResultCode.APP_ERROR_TOKEN_INVALID);
        }
        return client.login(vo);
    }

    @Override
    public Result logout(UserLoginBeanVO userLoginBeanVO) {
        stringRedisTemplate.delete(userLoginBeanVO.getToken());
        return Result.success();
    }
}
