package com.example.lib_net.request;

import com.example.lib_net.module.HttpMethod;
import com.example.lib_net.request.base.NoBodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 16244 on 2019/3/8.
 */

public class HeadRequest<T> extends NoBodyRequest<T,HeadRequest<T>> {
    public HeadRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.HEAD;
    }

    @Override
    protected Request generateRequest(RequestBody requestBody) {
        Request.Builder  builder = generateRequestBuilder(requestBody);
        return builder.head().url(mUrl).tag(mTag).build();
    }
}
