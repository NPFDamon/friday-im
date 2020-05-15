package com.friday.route.redis;

import com.friday.server.bean.im.ServerInfo;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-15:13:57
 */
public interface UserServerRedisService {

    void addUserToServer(String uid, ServerInfo serverInfo);

    void removeUserFromServer(String uid,ServerInfo serverInfo);

    void serverOffLine(ServerInfo serverInfo);

    ServerInfo getServerInfoByUid(String uid);

}
