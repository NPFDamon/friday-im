package com.friday.common.netty.impl;

import com.friday.common.netty.NettyAttrUtil;
import com.friday.common.netty.UidChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
@Slf4j
public class UidChannelManagerImpl implements UidChannelManager {
    private static final Map<String, List<Channel>> map = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addUserToChannel(String uid, Channel channel) {
        NettyAttrUtil.setAttrKeyUid(channel, uid);
        log.info("uid:{} channel:{} save relation", uid, channel.remoteAddress());
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
        log.info("map:{}",map);
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
