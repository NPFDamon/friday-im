package com.friday.route.lb.hash;

import com.friday.route.lb.ServerRouteLoadBalanceHandler;
import com.friday.common.bean.im.ServerInfo;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Copyright (C),Damon
 *
 * @Description: 一致性hash
 * @Author: Damon(npf)
 * @Date: 2020-05-14:11:13
 */
public class ConsistentHashLoadBalanceHandler implements ServerRouteLoadBalanceHandler {

    // 使用murmur哈希算法
    private static final HashFunction hashFunction = Hashing.murmur3_32();

    private static final Charset charset = CharsetUtil.UTF_8;

    private static final int VIRTUAL_NODE_SIZE = 8;

    private static final String VIRTUAL_NODE_SUFFIX = "$$";


    @Override
    public ServerInfo routeServer(List<ServerInfo> serverInfos, String key) {
        HashCode hashCode = hashFunction.hashString(key, charset);
        TreeMap<Integer, ServerInfo> ring = getRing(serverInfos);
        return location(ring, hashCode.asInt());
    }

    public ServerInfo location(TreeMap<Integer, ServerInfo> ring, int invocationHashCode) {
        //顺时针找到第一个key
        Map.Entry<Integer, ServerInfo> entry = ring.ceilingEntry(invocationHashCode);
        if (entry == null) {
            //环形，超出选第一个节点
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    public TreeMap<Integer, ServerInfo> getRing(List<ServerInfo> serverInfos) {
        TreeMap<Integer, ServerInfo> ring = new TreeMap<>();
        for (ServerInfo info : serverInfos) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                HashCode hashCode = hashFunction.hashString(info.hashCode() + VIRTUAL_NODE_SUFFIX + i, charset);
                ring.put(hashCode.asInt(), info);
            }
        }
        return ring;
    }
}
