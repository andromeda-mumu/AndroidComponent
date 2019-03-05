package com.example.lib_net.request;

import com.example.lib_net.module.HttpMethod;
import com.example.lib_net.request.base.NoBodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by wangjiao on 2019/3/5.
 */

public class GetRequest<T> extends NoBodyRequest<T,GetRequest<T>> {
    public GetRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected Request generateRequest(RequestBody requestBody) {
        //公共参数 公共header
        Request.Builder builder = generateRequestBuilder(requestBody);
        return builder.get().url(mUrl).tag(mTag).build();
    }
}
