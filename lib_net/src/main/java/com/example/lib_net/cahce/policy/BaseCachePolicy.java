package com.example.lib_net.cahce.policy;

import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.cahce.CacheMode;
import com.example.lib_net.db.CacheManager;
import com.example.lib_net.exception.HttpException;
import com.example.lib_net.utils.HeaderParser;
import com.example.lib_net.utils.HttpUtils;

import java.net.SocketTimeoutException;

import okhttp3.Call;
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
    private CacheEntity<T> cacheEntity;
    private Call mRawCall;

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



    /**--------------请求成功后 根据缓存模式更新缓存----------------*/
    private void saveCache(Headers headers, T body) {

    }

}
