package com.example.lib_net.callback;

import com.example.lib_net.base.Request;
import com.example.lib_net.convert.Convert;
import com.example.lib_net.module.Response;


/**
 * Created by wangjiao on 2019/3/5.
 * 请求的回调接口
 */

public interface Callback<T> extends Convert<T>{
    /**--------------ui线程----------------*/
    void onStart(Request<T,? extends Request> request);

    /**--------------数据回调 UI线程----------------*/
    void onSuccess(Response<T> response);
    void onCacheSuccess(Response<T> response);
    void onError(Response<T> response);
    void onFinish();

    /**--------------进度----------------*/
    void uploadProgress(Process process);
    void downloadProgress(Process process);
}
