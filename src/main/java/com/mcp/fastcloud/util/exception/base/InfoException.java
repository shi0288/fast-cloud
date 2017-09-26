package com.mcp.fastcloud.util.exception.base;


import com.mcp.fastcloud.util.Code;
import com.mcp.fastcloud.util.enums.Level;

public class InfoException extends BaseException {
    public InfoException(Code code) {
        super(code);
        this.level = Level.INFO;
    }
}
