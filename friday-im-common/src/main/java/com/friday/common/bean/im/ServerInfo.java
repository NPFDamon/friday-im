package com.friday.common.bean.im;

import com.friday.common.enums.NetTypeEnum;
import com.friday.common.utils.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Copyright (C),Damon
 *
 * @Description: server info
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo {
    private int tcpPort;
    private int httpPort;
    private int wsPort;
    private String ip;
    private NetTypeEnum netTypeEnum;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ServerInfo info = (ServerInfo) o;
        return tcpPort == info.tcpPort &&
                wsPort == info.wsPort &&
                httpPort == info.httpPort &&
                Objects.equals(ip, info.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, tcpPort, httpPort, wsPort, netTypeEnum);
    }

    @Override
    public String toString() {
        return JsonHelper.toJsonString(this);
    }
}
