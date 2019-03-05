package com.example.lib_net.module;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * Created by wangjiao on 2019/3/5.
 *封装请求头
 *
 * mmc:请求头有接收的类型，缓存时间，没了。。。。
 *
 */

public class HttpHeader implements Serializable {
    private static final long serialVersionUID = 5220766564242960066L;


    public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
    public static final String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";
    public static final String HEAD_KEY_RANGE = "Range";
    public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

    //这几个平时常见
    public static final String HEAD_KEY_DATE = "Date";
    public static final String HEAD_KEY_EXPIRES = "Expires";
    public static final String HEAD_KEY_E_TAGS = "Etag";
    public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";
    public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";
    public static final String HEAD_KEY_USER_AGENT = "User-Agent";

    public LinkedHashMap<String,String> mHeadersMap;

    public HttpHeader() {
        init();
    }
    public HttpHeader(String key,String value){
        init();
        put(key,value);
    }
    private void init() {
        mHeadersMap =new LinkedHashMap<>();
    }
    public void put(String key, String value) {
        if(key!=null && value!=null){
            mHeadersMap.put(key,value);
        }
    }

    public void put(HttpHeader header){
        if(header!=null && header.mHeadersMap!=null && !header.mHeadersMap.isEmpty()){
            mHeadersMap.putAll(header.mHeadersMap);
        }
    }


}
