package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.Code;
import com.mcp.fastcloud.util.enums.Level;

public class DebugException extends BaseException {
    public DebugException(Code code) {
        super(code);
        this.level = Level.DEBUG;
    }
}
