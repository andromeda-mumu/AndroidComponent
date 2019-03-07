package com.example.lib_net.utils;

import android.text.TextUtils;

import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.cahce.CacheMode;
import com.example.lib_net.module.HttpHeader;

import java.util.Locale;
import java.util.StringTokenizer;

import okhttp3.Headers;

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

    /**
     * 根据请求结果生成对应的缓存实体类，以下为缓存相关的响应头
     * Cache-Control: public                             响应被缓存，并且在多用户间共享
     * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享
     * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证
     * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）
     * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高
     * Date: Mon, 19 Nov 2012 08:39:00 GMT               当前response发送的时间
     * Expires: Mon, 19 Nov 2012 08:40:01 GMT            缓存过期的时间（绝对时间）
     * Last-Modified: Mon, 19 Nov 2012 08:38:01 GMT      服务器端文件的最后修改时间
     * ETag: "20b1add7ec1cd1:0"                          服务器端文件的ETag值
     * 如果同时存在cache-control和Expires，浏览器总是优先使用cache-control
     * @param headers
     * @param body
     * @param cacheMode
     * @param cacheKey
     * @param <T>
     * @return
     */

    public static <T> CacheEntity<T> createCacheEntity(Headers headers, T body, CacheMode cacheMode, String cacheKey) {
        long localExpire = 0; //缓存到期时间 本地时间
        if(cacheMode == CacheMode.DEFAULT){
            long date = HttpHeader.getDate(headers.get(HttpHeader.HEAD_KEY_DATE));
            long expires = HttpHeader.getExpiration(headers.get(HttpHeader.HEAD_KEY_EXPIRES));
            String cacheControl = HttpHeader.getCacheControl(headers.get(HttpHeader.HEAD_KEY_CACHE_CONTROL),headers.get(HttpHeader.HEAD_KEY_CACHE_CONTROL));

            //没有缓存头控制，不需要缓存
            if(TextUtils.isEmpty(cacheControl)&& expires<0)return null;

            long maxAge = 0;
            if(!TextUtils.isEmpty(cacheControl)){
                StringTokenizer tokens = new StringTokenizer(cacheControl);//yyy 没见过
                while (tokens.hasMoreTokens()){
                    String token = tokens.nextToken().trim().toLowerCase(Locale.getDefault());
                    if(token.equals("no-cache")||token.equals("no-store"))return null;//服务器指定不缓存
                    else if(token.startsWith("max-age=")){
                        try{

                            //获取最大缓存时间
                            maxAge = Long.parseLong(token.substring(8));
                            //如果服务器设置缓存过期
                            if(maxAge<0)return null;
                        }catch (Exception e){
                            OkLogger.printStackTrace(e);
                        }
                    }
                }
            }

            //获取基准时间，先去response的date,没有就使用本地时间
            long now = System.currentTimeMillis();
            if(date>0) now= date;
            if(maxAge>0){
                //http 1.1 优先验证cache-control
                localExpire = now+maxAge*1000;
            }else if(expires>0){
                //http 1.0 优先验证Expire
                localExpire = expires;
            }
        }else{
            localExpire = System.currentTimeMillis();
        }

        HttpHeader header = new HttpHeader();
        for (String headerName:headers.names()){
            header.put(headerName,headers.get(headerName));
        }

        //构建缓存实体
        CacheEntity<T> entity = new CacheEntity<>();
        entity.setLocalExpire(localExpire);
        entity.setKey(cacheKey);
        entity.setData(body);
        entity.setResponseHeader(header);
        return entity;
    }
}















