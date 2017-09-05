package com.mcp.fastcloud.util;


import com.mcp.fastcloud.util.enums.ResultCode;

/**
 * Created by shiqm on 2017/3/23.
 */
public class Result {

    private int code;
    private String msg;
    private Object data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Result() {
        this(ResultCode.OK);
    }


    public Result(Code code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public Result(String massage) {
        this.msg = massage;
    }

    public Result(Object data) {
        this(ResultCode.OK);
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
