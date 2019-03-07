package com.example.lib_net.cahce.policy;

import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.exception.CacheException;
import com.example.lib_net.module.Response;

import okhttp3.Call;

/**
 * Created by wangjiao on 2019/3/7.
 */

public class DefaultCachePolicy<T> extends BaseCachePolicy<T> {
    public DefaultCachePolicy(Request<T, ? extends Request> request) {
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
            return Response.error(throwable,false,mRawCall,null);
        }
        Response<T> response = requestNetworkSync();
        if(response.isSuccessful() && response.code()==304){
            if(cacheEntity==null){
                return Response.error(CacheException.NON_CACHE_304(cacheEntity.getKey()),false,mRawCall,response.getRawResponse());
            }else{
                return Response.success(cacheEntity.getData(),true,mRawCall,response.getRawResponse());
            }
        }
        return response;
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
                    throwable.printStackTrace();
                    Response<T> error = Response.error(throwable,false,mRawCall,null);
                    mCallback.onError(error);
                }
                requestNetworkAsyn();
            }
        });
    }

    @Override
    public boolean onAnalysisResponse(final Call call, final okhttp3.Response response) {
        if(response.code()!=304) return false;
        if(cacheEntity==null){
           final Response error = Response.error(CacheException.NON_CACHE_304(cacheEntity.getKey()),true,mRawCall,response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(error);
                    mCallback.onFinish();
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onCacheSuccess(Response.success(cacheEntity.getData(),true,mRawCall,response));
                    mCallback.onFinish();
                }
            });
        }
        return super.onAnalysisResponse(call, response);
    }
}
