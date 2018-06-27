package com.mcp.fastcloud.util.contract;

import feign.*;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * Created by shiqm on 2018-04-11.
 */
public class PostElectiveContract extends Contract.Default {


    private boolean isDefalutPost = false;


    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation,
                                             Method method) {
        Class<? extends Annotation> annotationType = methodAnnotation.annotationType();


        if (annotationType == RequestLine.class) {
            String requestLine = RequestLine.class.cast(methodAnnotation).value();
            checkState(emptyToNull(requestLine) != null,
                    "RequestLine annotation was empty on method %s.", method.getName());
            if (requestLine.indexOf(' ') == -1) {
                checkState(requestLine.indexOf('/') == -1,
                        "RequestLine annotation didn't start with an HTTP verb on method %s.",
                        method.getName());
                data.template().method(requestLine);
                return;
            }
            data.template().method(requestLine.substring(0, requestLine.indexOf(' ')));
            if (requestLine.indexOf(' ') == requestLine.lastIndexOf(' ')) {
                // no HTTP version is ok
                data.template().append(requestLine.substring(requestLine.indexOf(' ') + 1));
            } else {
                // skip HTTP version
                data.template().append(
                        requestLine.substring(requestLine.indexOf(' ') + 1, requestLine.lastIndexOf(' ')));
            }

            data.template().decodeSlash(RequestLine.class.cast(methodAnnotation).decodeSlash());

        } else if (annotationType == Body.class) {
            isDefalutPost = true;
            String body = Body.class.cast(methodAnnotation).value();
            checkState(emptyToNull(body) != null, "Body annotation was empty on method %s.",
                    method.getName());
            if (body.indexOf('{') == -1) {
                data.template().body(body);
            } else {
                data.template().bodyTemplate(body);
            }
        } else if (annotationType == Headers.class) {
            String[] headersOnMethod = Headers.class.cast(methodAnnotation).value();
            checkState(headersOnMethod.length > 0, "Headers annotation was empty on method %s.",
                    method.getName());
            data.template().headers(toMap(headersOnMethod));
        }
    }


    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        boolean isHttpAnnotation = false;
        boolean isGetNoUrlParma = false;
        String paramTag = null;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == Param.class) {
                Param paramAnnotation = (Param) annotation;
                String name = paramAnnotation.value();
                checkState(emptyToNull(name) != null, "Param annotation was empty on param %s.", paramIndex);
                nameParam(data, name, paramIndex);
                Class<? extends Param.Expander> expander = paramAnnotation.expander();
                if (expander != Param.ToStringExpander.class) {
                    data.indexToExpanderClass().put(paramIndex, expander);
                }
                data.indexToEncoded().put(paramIndex, paramAnnotation.encoded());
                isHttpAnnotation = true;
                String varName = '{' + name + '}';
                if (!data.template().url().contains(varName) &&
                        !searchMapValuesContainsSubstring(data.template().queries(), varName) &&
                        !searchMapValuesContainsSubstring(data.template().headers(), varName)) {
                    //增加post处理校验
                    if (data.template().method().equals("POST")) {
                        data.formParams().add(name);
                        //兼容默认配置处理-如果前边body不存在，这里为其自动配置
                        if (data.template().body() == null) {
                            paramTag = name + "=" + varName;
                        }
                    }
                    if (data.template().method().equals("GET")) {
                        //GET其实可以自动给url增加参数，不用在url注明，可以优化
                        paramTag = name + "=" + varName;
                        isGetNoUrlParma = true;
                    }
                }
            } else if (annotationType == QueryMap.class) {
                    checkState(data.queryMapIndex() == null, "QueryMap annotation was present on multiple parameters.");
                    data.queryMapIndex(paramIndex);
                    data.queryMapEncoded(QueryMap.class.cast(annotation).encoded());
                    isHttpAnnotation = true;
            } else if (annotationType == HeaderMap.class) {
                checkState(data.headerMapIndex() == null, "HeaderMap annotation was present on multiple parameters.");
                data.headerMapIndex(paramIndex);
                isHttpAnnotation = true;
            }
        }
        if (StringUtils.isNotEmpty(paramTag)) {
            if (isGetNoUrlParma) {
                if (data.template().url().indexOf("?") == -1) {
                    data.template().append("?" + paramTag);
                } else {
                    data.template().append("&" + paramTag);
                }
            } else {
                if (data.template().bodyTemplate() != null) {
                    data.template().bodyTemplate(data.template().bodyTemplate() + "&" + paramTag);
                } else {
                    data.template().bodyTemplate(paramTag);
                }
            }
        }
        return isHttpAnnotation;
    }


    private static <K, V> boolean searchMapValuesContainsSubstring(Map<K, Collection<String>> map,
                                                                   String search) {
        Collection<Collection<String>> values = map.values();
        if (values == null) {
            return false;
        }
        for (Collection<String> entry : values) {
            for (String value : entry) {
                if (value.contains(search)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Map<String, Collection<String>> toMap(String[] input) {
        Map<String, Collection<String>>
                result =
                new LinkedHashMap<String, Collection<String>>(input.length);
        for (String header : input) {
            int colon = header.indexOf(':');
            String name = header.substring(0, colon);
            if (!result.containsKey(name)) {
                result.put(name, new ArrayList<String>(1));
            }
            result.get(name).add(header.substring(colon + 2));
        }
        return result;
    }


}
