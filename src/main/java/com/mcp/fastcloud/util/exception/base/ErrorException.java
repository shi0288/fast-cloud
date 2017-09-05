package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.enums.Level;
import com.mcp.fastcloud.util.enums.ResultCode;

public class ErrorException extends BaseException {
    public ErrorException(ResultCode resultCode) {
        super(resultCode);
        this.level = Level.ERROR;
    }
}
