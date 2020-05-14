package com.friday.route.cache;

import com.friday.route.zk.ZK;
import com.friday.server.bean.im.ServerInfo;
import com.friday.server.constant.Constants;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C),Damon
 *
 * @Description: server cache 服务器节点缓存
 * @Author: Damon(npf)
 * @Date: 2020-05-14:10:08
 */
@Component
@Slf4j
public class ServerCache {

    @Autowired
    private LoadingCache<String, String> cache;

    @Autowired
    private ZK zk;

    public void addCache(String key) {
        cache.put(key, key);
    }

    /**
     * 更新缓存
     * 先删除 再新增
     */
    public void updateCache(List<String> children) {
        cache.invalidateAll();
        for (String node : children) {
            String key;
            if (node.split("-").length == 2) {
                key = node.split("-")[1];
            } else {
                key = node;
            }
            addCache(key);
        }
    }

    /**
     * 获取所有服务器列表
     */
    public List<String> getServerList() {
        List<String> strings = new ArrayList<>();
        if (cache.size() == 0) {
            List<String> all = zk.getAllNode();
            for (String node : all) {
                String key = node.split("-")[1];
                addCache(key);
            }
        }
        for (Map.Entry<String, String> entry : cache.asMap().entrySet()) {
            strings.add(entry.getKey());
        }
        return strings;
    }

    /**
     * rebuild
     */
    public void rebuildCache() {
        updateCache(getServerList());
    }
}
