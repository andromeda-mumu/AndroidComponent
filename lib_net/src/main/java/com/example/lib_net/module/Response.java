package com.example.lib_net.module;

import okhttp3.Call;
import okhttp3.Headers;

/**
 * Created by wangjiao on 2019/3/5.
 */

public final class Response<T> {
    private T body;
    private Throwable mThrowable;
    private boolean mIsFromCache;
    private Call mRawCall;
    private okhttp3.Response mRawResponse;

    public static <T> Response<T> success(T body, boolean isFromCache, Call rawCall, okhttp3.Response rawResponse) {
        Response<T> response = new Response<>();
        response.setBody(body);
        response.setFromCache(isFromCache);
        response.setRawCall(rawCall);
        response.setRawResponse(rawResponse);
        return response;
    }
    public static <T> Response<T> error(Throwable throwable, boolean isFromCache, Call rawCall, okhttp3.Response rawResponse) {
        Response<T> response = new Response<>();
        response.setThrowable(throwable);
        response.setFromCache(isFromCache);
        response.setRawCall(rawCall);
        response.setRawResponse(rawResponse);
        return response;
    }


    public int code(){
        if(mRawResponse==null)return -1;
        return mRawResponse.code();
    }
    public String message(){
        if(mRawResponse==null) return null;
        return mRawResponse.message();
    }
    public Headers header(){
        if(mRawResponse==null) return null;
        return mRawResponse.headers();
    }
    public boolean isSuccessful(){
        return mThrowable ==null;
    }


    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
    public Throwable getThrowable() {
        return mThrowable;
    }

    public void setThrowable(Throwable throwable) {
        mThrowable = throwable;
    }

    public boolean isFromCache() {
        return mIsFromCache;
    }

    public void setFromCache(boolean fromCache) {
        mIsFromCache = fromCache;
    }

    public Call getRawCall() {
        return mRawCall;
    }

    public void setRawCall(Call rawCall) {
        mRawCall = rawCall;
    }

    public okhttp3.Response getRawResponse() {
        return mRawResponse;
    }

    public void setRawResponse(okhttp3.Response rawResponse) {
        mRawResponse = rawResponse;
    }
}
