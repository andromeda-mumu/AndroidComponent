package com.example.lib_net.exception;

import com.example.lib_net.module.Response;
import com.example.lib_net.utils.HttpUtils;

/**
 * Created by wangjiao on 2019/3/6.
 */

public class HttpException extends RuntimeException{

    private static final long serialVersionUID = -124285589041707641L;
    private int code;
    private String message;
    private transient Response<?> mResponse;

    public HttpException(String message){
        super(message);
    }
    public HttpException(Response<?> response){
        super(getMessage(response));
        this.code = response.code();
        this.message = response.message();
        this.mResponse = response;
    }
    public static String getMessage(Response<?> response){
        HttpUtils.checkNotNull(response,"response==null");
        return "HTTP "+response.code()+" "+response.message();
    }
    public static HttpException NET_ERROR() {
        return new HttpException("network error! http response code is 404 or 5xx!");
    }
    public static HttpException  COMMON(String message) {
        return new HttpException(message);
    }
}
