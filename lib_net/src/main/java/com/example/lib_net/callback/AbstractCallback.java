package com.example.lib_net.callback;

import android.util.Log;

import com.example.lib_net.base.Request;
import com.example.lib_net.module.Response;
import com.example.lib_net.utils.OkLogger;


/**
 * Created by wangjiao on 2019/3/5.
 */

public abstract class AbstractCallback<T> implements Callback<T> {

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        Log.d("=mmc=","----start----");
    }

    @Override
    public void onCacheSuccess(Response<T> response) {
        Log.d("=mmc=","----cacthe----");
    }

    @Override
    public void onError(Response<T> response) {
        OkLogger.printStackTrace(response.getThrowable());
        Log.d("=mmc=","----onError----");
    }

    @Override
    public void onFinish() {
        Log.d("=mmc=","----finish----");
    }

    @Override
    public void uploadProgress(Process process) {

    }

    @Override
    public void downloadProgress(Process process) {

    }
}
