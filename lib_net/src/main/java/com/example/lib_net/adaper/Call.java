package com.example.lib_net.adaper;

import com.example.lib_net.base.Request;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.module.Response;

/**
 * Created by wangjiao on 2019/3/5.
 * 请求的包装类
 */

public interface Call<T> {
    /**--------------同步执行----------------*/
    Response<T> execute() throws Exception;

    /**--------------异步执行 回调----------------*/
    void execute(Callback<T> callback);

    boolean isEexcuted();
    void cancel();
    boolean isCancel();
    Call<T> clone();
    Request getRequest();

}
