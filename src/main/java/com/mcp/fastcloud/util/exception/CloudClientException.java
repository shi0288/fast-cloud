package com.mcp.fastcloud.util.exception;


import com.mcp.fastcloud.util.enums.ResultCode;
import com.mcp.fastcloud.util.exception.base.ErrorException;

/**
 * Created by shiqm on 2017-07-12.
 */
public class CloudClientException extends ErrorException {
    public CloudClientException() {
        super(ResultCode.ERROR_CLOUD);
    }
}
