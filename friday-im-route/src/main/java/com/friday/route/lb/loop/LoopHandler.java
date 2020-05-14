package com.friday.route.lb.loop;

import com.friday.route.lb.ServerRouteHandler;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.enums.LoginStatusEnum;
import com.friday.server.exception.BizException;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright (C),Damon
 *
 * @Description: loop
 * @Author: Damon(npf)
 * @Date: 2020-05-14:11:09
 */
public class LoopHandler implements ServerRouteHandler {
    private AtomicLong atomicLong = new AtomicLong();

    @Override
    public ServerInfo routeServer(List<ServerInfo> serverInfos, String key) {
        if (serverInfos.size() == 0) {
            throw new BizException(LoginStatusEnum.SERVER_NOT_AVAILABLE.getMsg());
        }
        long position = atomicLong.incrementAndGet() % serverInfos.size();
        if (position < 0L) {
            position = 1L;
        }
        return serverInfos.get((int) position);
    }
}
