package com.example.lib_net.exception;

/**
 * Created by wangjiao on 2019/3/7.
 */

public class CacheException extends Exception {

    private static final long serialVersionUID = 8369784318036391642L;
    public CacheException(String msg){
        super(msg);
    }
    public static CacheException NON_CACHE_304(String cacheKey){
        return new CacheException("the http response code is 304, but the cache with cacheKey = " + cacheKey + " is null or expired!");
    }

}
