package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.enums.Level;
import com.mcp.fastcloud.util.enums.ResultCode;

/**
 * Created by shiqm on 2017/3/23.
 */
public class BaseException extends RuntimeException {

    protected ResultCode resultCode;
    protected Level level = Level.ERROR;


    public BaseException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
