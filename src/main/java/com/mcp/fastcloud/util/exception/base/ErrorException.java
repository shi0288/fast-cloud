package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.Code;
import com.mcp.fastcloud.util.enums.Level;
import com.mcp.fastcloud.util.enums.ResultCode;

public class ErrorException extends BaseException {
    public ErrorException(Code code) {
        super(code);
        this.level = Level.ERROR;
    }
}
