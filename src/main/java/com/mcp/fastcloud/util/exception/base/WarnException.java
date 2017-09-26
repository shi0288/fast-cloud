package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.Code;
import com.mcp.fastcloud.util.enums.Level;

public class WarnException extends BaseException {
    public WarnException(Code code) {
        super(code);
        this.level = Level.WARN;
    }
}
