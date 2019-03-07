package com.example.lib_net.cahce.policy;

import android.graphics.Bitmap;

import com.example.lib_net.OkClient;
import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.cahce.CacheMode;
import com.example.lib_net.db.CacheManager;
import com.example.lib_net.exception.HttpException;
import com.example.lib_net.utils.HeaderParser;
import com.example.lib_net.utils.HttpUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by wangjiao on 2019/3/6.
 */

public abstract class  BaseCachePolicy<T> implements CachePolicy<T> {
    protected Request<T,? extends Request> mRequest;
    protected volatile boolean mCanceled;
    protected volatile int mCurrentRetryCount =0;
    protected boolean mExecuted;
    protected CacheEntity<T> cacheEntity;
    protected Call mRawCall;
    protected com.example.lib_net.callback.Callback<T> mCallback;

    public BaseCachePolicy(Request<T,? extends Request> request){
        this.mRequest = request;
    }

    @Override
    public boolean onAnalysisResponse(Call call, Response response) {
        return false;
    }

    @Override
    public CacheEntity<T> prepareCache() {
        if(mRequest.getCacheKey() ==null){
            mRequest.cacheKey(HttpUtils.createUrlFromParams(mRequest.getBaseUrl(),mRequest.getHttpParams().urlParamsMap));
        }
        if(mRequest.getCacheMode()==null){
            mRequest.cacheMode(CacheMode.NO_CACHE);
        }
        CacheMode cacheMode = mRequest.getCacheMode();
        if(cacheMode!=CacheMode.NO_CACHE){
           cacheEntity = (CacheEntity<T>) CacheManager.getInstance().get(mRequest.getCacheKey());
            HeaderParser.addCacheHeaders(mRequest,cacheEntity,cacheMode);
            if(cacheEntity!=null && cacheEntity.checkExpire(cacheMode,mRequest.getCacheTime(),System.currentTimeMillis())){
                cacheEntity.setExpire(true);
            }
        }

        if(cacheEntity==null||cacheEntity.isExpire() || cacheEntity.getData()==null || cacheEntity.getResponseHeader()==null){
           cacheEntity = null;
        }
        return cacheEntity;
    }

    @Override
    public Call prepareRawCall() throws Throwable {
        if(mExecuted) throw HttpException.COMMON("Already execyted!");
        mExecuted = true;
        mRawCall = mRequest.getRawCall();
        if(mCanceled)
            mRawCall.cancel();
        return mRawCall;
    }

    /**--------------同步网络请求----------------*/
    protected com.example.lib_net.module.Response<T> requestNetworkSync(){
        try {
           Response response = mRawCall.execute();
           int code = response.code();
           //网络错误
            if(code==404 || code>=500){
                return com.example.lib_net.module.Response.error(HttpException.NET_ERROR(),false,mRawCall,response);
            }
            T body = mRequest.getConverter().convertResponse(response);
            saveCache(response.headers(),body);
            return com.example.lib_net.module.Response.success(body,false,mRawCall,response);
        } catch (Exception e) {
            if(e instanceof SocketTimeoutException && mCurrentRetryCount<mRequest.getRetryCount()){
                mCurrentRetryCount++;
                mRawCall = mRequest.getRawCall();
                if(mCanceled){
                    mRawCall.cancel();
                }else {
                    requestNetworkSync();
                }
            }
            return com.example.lib_net.module.Response.error(e,false,mRawCall,null);
        }
    }


    /**--------------异步网络请求----------------*/
    protected void requestNetworkAsyn(){
        mRawCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //重试
                if(e instanceof SocketTimeoutException && mCurrentRetryCount<mRequest.getRetryCount()){
                    mRawCall = mRequest.getRawCall();
                    if(mCanceled){
                        mRawCall.cancel();
                    }else{
                        mRawCall.enqueue(this);
                    }
                }else{//构建自己的错误
                    if(!call.isCanceled()){
                        com.example.lib_net.module.Response<T> error = com.example.lib_net.module.Response.error(e,false,mRawCall,null);
                        onError(error);
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                if(responseCode==404 || responseCode>=500){
                    com.example.lib_net.module.Response<T> error = com.example.lib_net.module.Response.error(HttpException.NET_ERROR(),false,mRawCall,response);
                    onError(error);
                    return;
                }
                if(onAnalysisResponse(mRawCall,response)) return;

                T body = mRequest.getConverter().convertResponse(response);
                saveCache(response.headers(),body);
                com.example.lib_net.module.Response<T> success = com.example.lib_net.module.Response.success(body,false,mRawCall,response);
                onSuccess(success);
            }
        });
    }

    /**--------------请求成功后 根据缓存模式更新缓存----------------*/
    private void saveCache(Headers headers, T body) {
        if(mRequest.getCacheMode()==CacheMode.NO_CACHE) return;
        if(body instanceof Bitmap ) return;//bitmap没有实现serializable 不进行缓存 但是它实现了parcelable  yyy
        CacheEntity<T> cache = HeaderParser.createCacheEntity(headers,body,mRequest.getCacheMode(),mRequest.getCacheKey());
        if(cache==null){
            //服务器不需要缓存，移除本地缓存
            CacheManager.getInstance().remove(mRequest.getCacheKey());
        }else{
            CacheManager.getInstance().replace(mRequest.getCacheKey(),cache);
        }
    }

    @Override
    public void cancel() {
        mCanceled = true;
        if(mRawCall!=null){
            mRawCall.cancel();
        }
    }

    @Override
    public boolean isCancel() {
        if(mCanceled) return  true;
        synchronized (this){
            return mRawCall!=null && mRawCall.isCanceled();
        }
    }

    protected void runOnUiThread(Runnable runnable){
        OkClient.getInstance().getDelivery().post(runnable);
    }
    @Override
    public boolean isExecuted() {
        return mExecuted;
    }

}
