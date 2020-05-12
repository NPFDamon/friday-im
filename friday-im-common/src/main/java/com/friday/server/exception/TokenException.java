package com.friday.server.exception;

/**
 * Copyright (C),Damon
 *
 * @Description: token exception
 * @Author: Damon(npf)
 * @Date: 2020-05-11:14:37
 */
public class TokenException extends RuntimeException {
    public TokenException(String message, Throwable e) {
        super(message, e);
    }

    public TokenException(Throwable e) {
        super(e);
    }

    public TokenException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
