package com.friday.common.netty.impl;

import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.netty.UidChannelManager;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-15:14:48
 */
@Component
public class UidChannelManagerImpl implements UidChannelManager {
    private static final Map<String, List<Channel>> map = new ConcurrentHashMap<>();

    @Override
    public void addUserToChannel(String uid, Channel channel) {
        NettyAttrUtil.setAttrKeyUid(channel, uid);
        if (!map.containsKey(uid)) {
            List<Channel> channels = new ArrayList<>();
            channels.add(channel);
            map.put(uid, channels);
            return;
        }
        map.get(uid).add(channel);
    }

    @Override
    public List<Channel> getChannelById(String uid) {
        if (map.containsKey(uid)) {
            return map.get(uid);
        }
        return null;
    }

    @Override
    public String getIdByChannel(Channel channel) {
        return NettyAttrUtil.getUid(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        String uid = NettyAttrUtil.getUid(channel);
        if (uid != null) {
            map.remove(uid);
        }
    }

    @Override
    public List<String> getAllIds() {
        List<String> list = new ArrayList<>();
        map.forEach((key, value) -> list.add(key));
        return list;
    }

    @Override
    public List<Channel> getAllChannels() {
        List<Channel> list = new ArrayList<>();
        map.forEach((key, value) -> list.addAll(value));
        return list;
    }
}
