package com.example.lib_net.utils;

import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.cahce.CacheMode;
import com.example.lib_net.module.HttpHeader;

/**
 * Created by wangjiao on 2019/3/6.
 *
 */

public class HeaderParser {
    /**
     * 对每个请求添加默认的请求头，如果有缓存，并返回缓存实体对象
     * Cache-Control: max-age=0                            以秒为单位
     * If-Modified-Since: Mon, 19 Nov 2012 08:38:01 GMT    缓存文件的最后修改时间。
     * If-None-Match: "0693f67a67cc1:0"                    缓存文件的ETag值
     * Cache-Control: no-cache                             不使用缓存
     * Pragma: no-cache                                    不使用缓存
     * Accept-Language: zh-CN,zh;q=0.8                     支持的语言
     * User-Agent:                                         用户代理，它的信息包括硬件平台、系统软件、应用软件和用户个人偏好
     * Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36
     *
     * @param request 请求类
     * @param cacheEntity 缓存实体类
     * @param cacheMode
     * @param <T>
     */

    public static <T> void addCacheHeaders(Request<T, ? extends Request> request, CacheEntity<T> cacheEntity, CacheMode cacheMode) {
            //按照标准的http协议，添加304请求头
        if(cacheEntity!=null && cacheMode==CacheMode.DEFAULT){
            HttpHeader responseHeader = cacheEntity.getResponseHeader();
            if(responseHeader!=null){
                String eTag = responseHeader.get(HttpHeader.HEAD_KEY_E_TAGS);
                if(eTag!=null)
                    request.headers(HttpHeader.HEAD_KEY_E_TAGS,eTag);
                long lastModified = HttpHeader.getLastModified(responseHeader.get(HttpHeader.HEAD_KEY_LAST_MODIFIED));
                if(lastModified>0)
                    request.headers(HttpHeader.HEAD_KEY_LAST_MODIFIED,HttpHeader.formatMillisToGMT(lastModified));
            }
        }
    }
}
