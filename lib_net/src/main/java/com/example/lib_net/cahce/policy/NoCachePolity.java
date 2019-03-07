package com.example.lib_net.cahce.policy;

import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.module.Response;

/**
 * Created by wangjiao on 2019/3/7.
 */

public class NoCachePolity<T> extends BaseCachePolicy<T> {
    public NoCachePolity(Request<T, ? extends Request> request) {
        super(request);
    }

    @Override
    public void onSuccess(final Response<T> response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(response);
                mCallback.onFinish();
            }
        });
    }

    @Override
    public void onError(final Response<T> response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(response);
                mCallback.onFinish();
            }
        });
    }

    @Override
    public Response<T> requestSync(CacheEntity<T> cacheEntity) {
        try {
            prepareRawCall();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return requestNetworkSync();
    }

    @Override
    public void responseAsyn(CacheEntity<T> cacheEntity, Callback<T> callback) {
        mCallback = callback;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onStart(mRequest);
                try {
                    prepareRawCall();
                } catch (Throwable throwable) {
                    Response<T> error = Response.error(throwable,false,mRawCall,null);
                    mCallback.onError(error);
                    return;
                }
                requestNetworkAsyn();
            }
        });
    }
}
