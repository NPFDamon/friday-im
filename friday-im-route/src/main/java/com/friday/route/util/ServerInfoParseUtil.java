package com.friday.route.util;

import com.friday.common.bean.im.ServerInfo;
import com.friday.common.constant.Constants;
import com.friday.common.exception.BizException;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C),Damon
 *
 * @Description: util
 * @Author: Damon(npf)
 * @Date: 2020-05-14:14:31
 */
public class ServerInfoParseUtil {
    public static ServerInfo getServerInfo(String serverInfo) {
        ServerInfo server = new ServerInfo();
        try {
            server.setIp(serverInfo.split(Constants.DEFAULT_SEPARATES)[0]);
            server.setPort(Integer.parseInt(serverInfo.split(Constants.DEFAULT_SEPARATES)[1]));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("parse server fail ...");
        }
        return server;
    }

    public static List<ServerInfo> getServerInfoList(List<String> strings) {
        List<ServerInfo> serverInfos = new ArrayList<>();
        strings.forEach(s -> serverInfos.add(getServerInfo(s)));
        return serverInfos;
    }
}
