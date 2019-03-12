package com.example.lib_net.utils;

import android.text.TextUtils;

import com.example.lib_net.OkClient;
import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.HttpParams;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    /**--------------生成类似表单的请求体----------------*/
    public static RequestBody generateMultipartRequestBody(HttpParams httpParams, boolean isMultipart) {
        if(httpParams.fileParamsMap.isEmpty() && !isMultipart){
            //表单提交，但没有文件
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (Map.Entry<String,List<String>> entry:httpParams.urlParamsMap.entrySet()){
                List<String> values = entry.getValue();
                for (String value:values){
                    formBuilder.add(entry.getKey(),value);
                }
            }
            return formBuilder.build();
        }else{
            //表单提交 有文件
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            //拼接键值对
            for (Map.Entry<String,List<String>> entry:httpParams.urlParamsMap.entrySet()){
                List<String> values = entry.getValue();
                for (String value:values){
                    multipartBodyBuilder.addFormDataPart(entry.getKey(),value);
                }
            }
            //拼接文件
            for(Map.Entry<String,List<HttpParams.FileWrapper>> entry:httpParams.fileParamsMap.entrySet()){
                List<HttpParams.FileWrapper> list = entry.getValue();
                for (HttpParams.FileWrapper wrapper:list){
                    RequestBody fileBody = RequestBody.create(wrapper.contentType,wrapper.file);
                    multipartBodyBuilder.addFormDataPart(entry.getKey(),wrapper.filename,fileBody);
                }
            }
            return multipartBodyBuilder.build();
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        OkClient.getInstance().getDelivery().post(runnable);
    }

    /** 根据响应头或者url获取文件名 */
    public static String getNetFileName(Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (TextUtils.isEmpty(fileName)) fileName = getUrlFileName(url);
        if (TextUtils.isEmpty(fileName)) fileName = "unknownfile_" + System.currentTimeMillis();
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            OkLogger.printStackTrace(e);
        }
        return fileName;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    private static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header(HttpHeader.HEAD_KEY_CONTENT_DISPOSITION);
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replaceAll("\"", "");
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
            }
            split = "filename*=";
            indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                String fileName = dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
                String encode = "UTF-8''";
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length(), fileName.length());
                }
                return fileName;
            }
        }
        return null;
    }
}
