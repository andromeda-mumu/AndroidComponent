package com.example.lib_net.cahce;

/**
 * Created by wangjiao on 2019/3/6.
 */

public enum  CacheMode {
    /**--------------默认http协议的默认缓存  列如304响应头的时候 缓存----------------*/
    DEFAULT,
    /**--------------不缓存----------------*/
    NO_CACHE,
    /**--------------请求失败后 读取缓存----------------*/
    REQUEST_FAILED_READ_CACHE,
    /**--------------如果没有缓存就去网络请求----------------*/
    IF_NONE_CACHE_REQUEST,
    /**--------------先使用缓存，再去请求网络----------------*/
    FIRST_CACHE_THEN_REQUEST,

}
