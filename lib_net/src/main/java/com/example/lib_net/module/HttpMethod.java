package com.example.lib_net.module;

/**
 * Created by wangjiao on 2019/3/5.
 */

public enum  HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");


    private final String value;
    HttpMethod(String value) {
        this.value = value;
    }
    public boolean hasBody(){
        switch (this){
            case POST:
            case PUT:
            case PATCH:
            case DELETE:
            case OPTIONS:
                return true;
            default:
                return false;
        }
    }

}
