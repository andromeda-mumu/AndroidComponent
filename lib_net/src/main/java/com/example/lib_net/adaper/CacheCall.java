package com.example.lib_net.adaper;

import com.example.lib_net.base.Request;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.module.Response;

/**
 * Created by wangjiao on 2019/3/5.
 *
 * 带缓存的请求
 */

public class CacheCall<T> implements Call<T> {

    private Request<T,? extends Request> mRequest;
    public CacheCall(Request<T,? extends Request> request){
        this.mRequest = request;
    }
    @Override
    public Response<T> execute() throws Exception {
        return null;
    }

    @Override
    public void execute(Callback<T> callback) {

    }

    @Override
    public boolean isEexcuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request getRequest() {
        return null;
    }
}
