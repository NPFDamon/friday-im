package com.friday.server.enums;

/**
 * Copyright (C),Damon
 *
 * @Description: net type
 * @Author: Damon(npf)
 * @Date: 2020-05-13:14:49
 */
public enum NetTypeEnum {
    TCP("tcp"),
    WEB_SOCKET("web_socket"),
    INTERNAL("internal");

    private String type;

    private NetTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
