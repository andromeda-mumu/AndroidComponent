package com.example.lib_net.callback;

import okhttp3.Response;

/**
 * Created by wangjiao on 2019/3/5.
 * 默认将数据解析成javabean
 */

public class JsonCallback<T> extends AbstractCallback<T> {

    @Override
    public T convertResponse(Response response) {
        return null;
    }

    @Override
    public void onSuccess(com.example.lib_net.module.Response<T> response) {

    }
}
