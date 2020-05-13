package com.friday.server.enums;

public enum LoginEnum {
    SUCCESS("8000", "login success"),
    FAIL("4000", "login fail"),
    REPEAT_LOGIN("5000", "repeat login"),
    /**
     * 请求限流
     */
    REQUEST_LIMIT("6000", "请求限流"),

    /**
     * 账号不在线
     */
    OFF_LINE("7000", "你选择的账号不在线，请重新选择！"),

    /**
     * 服务不可用
     */
    SERVER_NOT_AVAILABLE("7100", "cim server is not available, please try again later!"),

    /**
     * 重连失败
     */
    RECONNECT_FAIL("7200", "Reconnect fail, continue to retry!"),
    /**
     * 登录信息不匹配
     */
    ACCOUNT_NOT_MATCH("9100", "The User information you have used is incorrect!");

    private String code;
    private String msg;

    private LoginEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static LoginEnum getStatus(String code) {
        for (LoginEnum loginEnum : values()) {
            if (loginEnum.getCode().equals(code)) {
                return loginEnum;
            }
        }
        throw new IllegalArgumentException("Login Status Enum Not legal" + code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
