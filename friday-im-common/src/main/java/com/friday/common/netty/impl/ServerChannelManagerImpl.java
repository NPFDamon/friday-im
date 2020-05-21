package com.friday.common.netty.impl;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.netty.ServerChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (C),Damon
 *
 * @Description: server channel manager 保存Server与channel关系
 * @Author: Damon(npf)
 * @Date: 2020-05-15:11:09
 */
@Slf4j
@Component
public class ServerChannelManagerImpl implements ServerChannelManager {
    private static final Map<ServerInfo, Channel> serverChannelMap = new ConcurrentHashMap<>();
    private static final Map<Channel, ServerInfo> channelServerMap = new ConcurrentHashMap<>();

    @Override
    public void addServerToChannel(ServerInfo serverInfo, Channel channel) {
        serverChannelMap.put(serverInfo, channel);
        channelServerMap.put(channel, serverInfo);
    }

    @Override
    public Channel getChannelByServer(ServerInfo serverInfo) {
        return serverChannelMap.get(serverInfo);
    }

    @Override
    public ServerInfo getServerByChannel(Channel channel) {
        return channelServerMap.get(channel);
    }

    @Override
    public void removeServer(ServerInfo serverInfo) {
        Channel channel = serverChannelMap.get(serverInfo);
        serverChannelMap.remove(serverInfo);
        channelServerMap.remove(channel);
    }
}
