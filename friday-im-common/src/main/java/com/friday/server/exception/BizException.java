package com.friday.server.exception;

/**
 * Copyright (C),Damon
 *
 * @Description: 业务异常
 * @Author: Damon(npf)
 * @Date: 2020-05-11:15:00
 */
public class BizException extends RuntimeException {
    public BizException(String errorMsg, Object... args) {
        super(String.format(errorMsg, args));
    }

    public BizException(String errorMessage, Exception cause, Object... args) {
        super(String.format(errorMessage, args), cause);
    }

    public BizException(Exception cause) {
        super(cause.getMessage(), cause);
    }
}
