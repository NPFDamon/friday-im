package com.friday.route.lb;

import com.friday.common.bean.im.ServerInfo;

import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description: server 负载均衡
 * @Author: Damon(npf)
 * @Date: 2020-05-14:10:08
 */
public interface ServerRouteLoadBalanceHandler {

    ServerInfo routeServer(List<ServerInfo> serverInfos, String key);
}
