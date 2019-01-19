package com.mcp.fastcloud.util.enums;

import com.mcp.fastcloud.util.Code;

/**
 * Created by shiqm on 2017/3/23.
 */
public enum ResultCode implements Code{

    OK(10000, "成功"),
    OVER(9999, "未知错误"),
    ERROR_CLOUD(100, "微服务通信错误"),
    ERROR_PARAMS(101, "参数错误");

    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
