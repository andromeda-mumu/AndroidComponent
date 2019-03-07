package com.example.lib_net.adaper;

import com.example.lib_net.base.Request;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.cahce.policy.CachePolicy;
import com.example.lib_net.cahce.policy.NoCachePolity;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.module.Response;
import com.example.lib_net.utils.HttpUtils;

/**
 * Created by wangjiao on 2019/3/5.
 *
 * 带缓存的请求
 */

public class CacheCall<T> implements Call<T> {

    private Request<T,? extends Request> mRequest;
    private CachePolicy<T> mCachePolicy;
    private CachePolicy<T> mPolicy;

    public CacheCall(Request<T,? extends Request> request){
        this.mRequest = request;
        this.mCachePolicy = preparePolicy();
    }

    @Override
    public Response<T> execute(){
        CacheEntity<T> entity = mCachePolicy.prepareCache();
        return mCachePolicy.requestSync(entity);
    }

    @Override
    public void execute(Callback<T> callback) {
        HttpUtils.checkNotNull(callback,"callback==null");

        CacheEntity<T> entity = mCachePolicy.prepareCache();
        mCachePolicy.responseAsyn(entity,callback);
    }

    private CachePolicy<T> preparePolicy() {
        switch (mRequest.getCacheMode()){
            case DEFAULT:
//                mPolicy = new DefaultCachePolicy<>(mRequest);
            case NO_CACHE:
            case IF_NONE_CACHE_REQUEST:
            case FIRST_CACHE_THEN_REQUEST:
            case REQUEST_FAILED_READ_CACHE:
                mPolicy = new NoCachePolity<>(mRequest);
        }
        if(mRequest.getCachePolicy()!=null){
            mPolicy = mRequest.getCachePolicy();
        }
        HttpUtils.checkNotNull(mPolicy,"policy == null");
        return mPolicy;
    }
    @Override
    public boolean isEexcuted() {
        return false;
    }

    @Override
    public void cancel() {
        mCachePolicy.cancel();
    }

    @Override
    public boolean isCancel() {
        return mCachePolicy.isCancel();
    }

    @Override
    public Call<T> clone() {
        return new CacheCall<>(mRequest);
    }

    @Override
    public Request getRequest() {
        return mRequest;
    }
}
