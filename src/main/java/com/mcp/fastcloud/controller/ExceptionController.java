package com.mcp.fastcloud.controller;

import com.mcp.fastcloud.annotation.Log;
import com.mcp.fastcloud.util.Result;
import com.mcp.fastcloud.util.enums.ResultCode;
import com.mcp.fastcloud.util.exception.base.BaseException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shiqm on 2017/3/23.
 */
public class ExceptionController {

    @Log
    protected Logger logger;

    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest req, Exception ex) {
        Throwable e = ExceptionUtils.getRootCause(ex);
        if (null != e) {
            ex = (Exception) e;
        }
        Result result;
        if (BaseException.class.isAssignableFrom(ex.getClass())) {
            BaseException baseException = (BaseException) ex;
            switch (baseException.getLevel()) {
                case INFO:
                    logger.info(baseException.getCode().toString());
                    break;
                case WARN:
                    logger.warn(baseException.getCode().toString());
                    break;
                case DEBUG:
                    logger.debug(baseException.getCode().toString());
                    break;
                case ERROR:
                    logger.error(baseException.getCode().toString());
                    break;
                default:
                    logger.error(baseException.getCode().toString());
            }
            result = new Result(baseException.getCode());
        }
        else {
            if (ex.getClass().isAssignableFrom(MissingServletRequestParameterException.class)) {
                MissingServletRequestParameterException msrpException = (MissingServletRequestParameterException) ex;
                logger.warn("请求:" + req.getRequestURI() + " 缺少参数：" + msrpException.getParameterName());
                result = new Result(ResultCode.ERROR_PARAMS);
            } else {
                logger.error(ExceptionUtils.getStackTrace(ex));
                result = new Result(ResultCode.OVER);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public interface ExceptionFilter {
        boolean matches(Exception ex);
    }


}
