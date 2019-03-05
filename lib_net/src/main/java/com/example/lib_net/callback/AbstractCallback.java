package com.example.lib_net.callback;

import com.example.lib_net.base.Request;
import com.example.lib_net.module.Response;
import com.example.lib_net.utils.OkLogger;


/**
 * Created by wangjiao on 2019/3/5.
 */

public abstract class AbstractCallback<T> implements Callback<T> {

    @Override
    public void onStart(Request<T, ? extends Request> request) {

    }

    @Override
    public void onCacheSuccess(Response<T> response) {

    }

    @Override
    public void onError(Response<T> response) {
        OkLogger.printStackTrace(response.getThrowable());
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void uploadProgress(Process process) {

    }

    @Override
    public void downloadProgress(Process process) {

    }
}
