package com.friday.common.bean.im;

import com.friday.common.enums.NetTypeEnum;
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
    private int port;
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
        return port == info.port && Objects.equals(ip, info.ip) && Objects.equals(netTypeEnum, info.netTypeEnum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, ip, netTypeEnum);
    }

    @Override
    public String toString() {
        return "{ \"port\":" + port
                + "\"ip\":" + ip
//                + "\"netTypeEnum\"" + netTypeEnum.getType()
                + "}";
    }
}
