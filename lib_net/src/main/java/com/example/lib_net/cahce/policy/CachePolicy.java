package com.example.lib_net.cahce.policy;

import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.module.Response;

import okhttp3.Call;

/**
 * Created by wangjiao on 2019/3/6.
 */

public interface CachePolicy<T> {
    /**--------------可以是网络或者缓存数据----------------*/
    void onSuccess(Response<T> response);

    /**--------------可以是缓存或网络----------------*/
    void onError(Response<T> response);

    /**--------------是否执行后续的回调----------------*/
    boolean onAnalysisResponse(Call call, okhttp3.Response response);

    /**--------------构建缓存----------------*/
    CacheEntity<T> prepareCache();

    /**--------------构建请求对象----------------*/
    Call prepareRawCall() throws Throwable;

    /**
     * -同步请求获得数据-
     * @param cacheEntity 本地缓存
     * @return
     */
    Response<T> requestSync(CacheEntity<T> cacheEntity);

    /**
     * 异步请求网络数据
     * @param cacheEntity
     * @param callback
     */
    void responseAsyn(CacheEntity<T> cacheEntity, Callback<T> callback);

    boolean isExecuted();
    boolean cancel();
    boolean isCancel();
}
