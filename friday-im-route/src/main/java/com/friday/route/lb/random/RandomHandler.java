package com.friday.route.lb.random;

import com.friday.route.lb.ServerRouteHandler;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.enums.LoginStatusEnum;
import com.friday.server.exception.BizException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Copyright (C),Damon
 *
 * @Description: random
 * @Author: Damon(npf)
 * @Date: 2020-05-14:11:05
 */
public class RandomHandler implements ServerRouteHandler {
    @Override
    public ServerInfo routeServer(List<ServerInfo> serverInfos, String key) {
        int size = serverInfos.size();
        if (size == 0) {
            throw new BizException(LoginStatusEnum.SERVER_NOT_AVAILABLE.getMsg());
        }
        int offset = ThreadLocalRandom.current().nextInt(size);
        return serverInfos.get(offset);
    }
}
