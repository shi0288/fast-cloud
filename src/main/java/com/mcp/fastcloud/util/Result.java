package com.mcp.fastcloud.util;


import com.mcp.fastcloud.util.enums.ResultCode;

/**
 * Created by shiqm on 2017/3/23.
 */
public class Result {

    private int code;
    private String message;
    private Object data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public Result() {
        this(ResultCode.OK);
    }


    public Result(Code code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public Result(String massage) {
        this.message = massage;
    }

    public Result(Object data) {
        this(ResultCode.OK);
        this.data = data;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
