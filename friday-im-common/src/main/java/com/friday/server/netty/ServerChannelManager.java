package com.friday.server.netty;

import com.friday.server.bean.im.ServerInfo;
import io.netty.channel.Channel;


/**
 * Copyright (C),Damon
 *
 * @Description: netty server channel manager
 * @Author: Damon(npf)
 * @Date: 2020-05-12:10:21
 */
public interface ServerChannelManager {
    void addServerToChannel(ServerInfo serverInfo, Channel channel);

    Channel getChannelByServer(ServerInfo serverInfo);

    ServerInfo getServerByChannel(Channel channel);

    void removeServer(ServerInfo serverInfo);
}
