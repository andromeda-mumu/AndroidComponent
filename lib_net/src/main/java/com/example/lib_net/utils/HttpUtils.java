package com.example.lib_net.utils;

import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.HttpParams;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;

/**
 * Created by wangjiao on 2019/3/5.
 */

public class HttpUtils {
    public static MediaType guessMimeType(String name) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        name.replace("#","");
        String contengType = fileNameMap.getContentTypeFor(name);
        if(contengType==null){
            return HttpParams.MEDIA_TYPE_STREAM;
        }else{
            return MediaType.parse(contengType);
        }
    }
    public static <T> T checkNotNull(T object ,String message){
        if(object==null)
            throw new NullPointerException(message);
        return object;
    }

    /**--------------把传递进来的参数拼接成URL----------------*/
    public static String createUrlFromParams(String baseUrl, LinkedHashMap<String, List<String>> urlParamsMap) {
        try{
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl);
            if(baseUrl.indexOf("&")>0 || baseUrl.indexOf("?")>0){
                sb.append("&");
            }else{
                sb.append("?");
            }
            for (Map.Entry<String,List<String>> urlparams:urlParamsMap.entrySet()){
                List<String> urlValues = urlparams.getValue();
                for (String url:urlValues){
                    //对参数进行utf-8编码 ,防止参数头传中文
                    String urlValue = URLEncoder.encode(url,"utf-8");
                    sb.append(urlparams.getKey()).append("=").append(url).append("&");
                }
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }catch (Exception e){
            OkLogger.printStackTrace(e);
        }
        return baseUrl;
    }

    /**--------------通用拼接请求头----------------*/
    public static Request.Builder appendHeader(Request.Builder builder, HttpHeader httpHeader) {
        if(httpHeader.mHeadersMap.isEmpty())return builder;
        Headers.Builder headerBuilder = new Headers.Builder();
        for(Map.Entry<String,String> header:httpHeader.mHeadersMap.entrySet()){
            headerBuilder.add(header.getKey(),header.getValue());
        }
        builder.headers(headerBuilder.build());
        return builder;
    }
}
