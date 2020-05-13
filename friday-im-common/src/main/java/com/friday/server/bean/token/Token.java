package com.friday.server.bean.token;

import com.friday.server.constant.Constants;
import com.friday.server.exception.TokenException;
import com.friday.server.utils.DesUtil;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Copyright (C),Damon
 *
 * @Description: tiken
 * @Author: Damon(npf)
 * @Date: 2020-05-11:14:48
 */
@Data
public class Token {
    private String id;
    private String key;
    private long time;

    public Token(String id, String key) {
        this.id = id;
        this.key = key;
        this.time = System.currentTimeMillis();
    }

    public String getToken(String secret) throws TokenException {
        String token = id + Constants.DEFAULT_SEPARATES + time + Constants.DEFAULT_SEPARATES + key;
        byte[] encryptToken = DesUtil.encrypt(secret.getBytes(), token.getBytes());
        return new String(Base64.getEncoder().encode(encryptToken), StandardCharsets.UTF_8);
    }
}
