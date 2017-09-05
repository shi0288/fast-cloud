package com.mcp.fastcloud.util;

import com.alibaba.fastjson.JSON;
import com.mcp.fastcloud.util.exception.CloudClientException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by shiqm on 2017-04-07.
 */
public class FastJsonDecoder implements Decoder {

    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.status() == 404) {
            throw new CloudClientException();
        } else if (response.body() == null) {
            throw new CloudClientException();
        } else {
            InputStream inputStream = response.body().asInputStream();
            Object obj = JSON.parseObject(inputStream, type);
            inputStream.close();
            return obj;
        }
    }
}
