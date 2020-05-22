package com.friday.common.bean.resVo;

import com.friday.common.enums.ResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-22:11:17
 */
@Data
public class Result implements Serializable {

    private static final long serialVersionUID = -4703795333211356196L;
    private Integer code;
    private String msg;
    private Object data;

    public Result(){}

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result success() {
        Result result = new Result();
        result.setResultCode(ResultCode.COMMON_SUCCESS);
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setResultCode(ResultCode.COMMON_SUCCESS);
        result.setData(data);
        return result;
    }

    public static Result failure(ResultCode resultCode) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setMsg(resultCode.getMsg());
        return result;
    }

    public static Result failure(ResultCode resultCode, String msg) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setMsg(msg);
        return result;
    }

    private void setResultCode(ResultCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

}
