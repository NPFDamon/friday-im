package com.friday.common.constant;

/**
 * Copyright (C),Damon
 *
 * @Description: 常量
 * @Author: Damon(npf)
 * @Date: 2020-05-11:11:03
 */
public class Constants {
    public static final String DEFAULT_CIPHER_ALGORITHM = "DES";
    public static final String DEFAULT_SEPARATES = ":";

    /**
     * redis 前缀
     */
    //token 信息
    public static final String USER_TOKEN = "user_token";
    //user server 对应关系
    public static final String USER_SERVER_INFO = "user_server_info:";
    //server channel对应关系
    public static final String SERVER_ROUTE_INFO = "server_route_info";
    //user client 对应关系
    public static final String USER_CLIENT_INFO = "user-client-info";

    //kafka topic
    public static final String KAFKA_TOPIC_SINGLE = "single";
    public static final String KAFKA_TOPIC_GROUP = "group";
}
