package com.friday.common.netty;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Copyright (C),Damon
 *
 * @Description: netty util
 * @Author: Damon(npf)
 * @Date: 2020-05-12:10:21
 */
public class NettyAttrUtil {
    private final static AttributeKey<String> ATTR_KEY_READ_TIME = AttributeKey.valueOf("readTime");
    private final static AttributeKey<String> ATTR_KEY_UID = AttributeKey.valueOf("uid");


    public static void updateReadTime(Channel channel, Long time) {
        channel.attr(ATTR_KEY_READ_TIME).set(String.valueOf(time));
    }

    public static Long getReadTime(Channel channel) {
        String value = getAttribute(channel, ATTR_KEY_READ_TIME);
        if (value != null) {
            return Long.valueOf(value);
        }
        return null;
    }

    public static String getAttribute(Channel channel, AttributeKey<String> key) {
        Attribute<String> value = channel.attr(key);
        return value.get();
    }

    public static void setAttrKeyUid(Channel channel, String uid) {
        channel.attr(ATTR_KEY_UID).set(uid);
    }

    public static String getUid(Channel channel) {
        String v = getAttribute(channel, ATTR_KEY_UID);
        return v == null ? null : v;
    }

}
