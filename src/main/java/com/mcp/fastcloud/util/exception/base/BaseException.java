package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.Code;
import com.mcp.fastcloud.util.enums.Level;
import com.mcp.fastcloud.util.enums.ResultCode;

/**
 * Created by shiqm on 2017/3/23.
 */
public class BaseException extends RuntimeException {

    protected Code code;
    protected Level level = Level.ERROR;

    public BaseException(Code code) {
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
