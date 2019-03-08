package com.example.lib_net.request;

import com.example.lib_net.module.HttpMethod;
import com.example.lib_net.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 16244 on 2019/3/8.
 */

public class PatchRequest<T> extends BodyRequest<T,PatchRequest<T>> {
    public PatchRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PATCH;
    }

    @Override
    protected Request generateRequest(RequestBody requestBody) {
        Request.Builder builder = generateRequestBuilder(requestBody);
        return builder.patch(requestBody).url(mUrl).tag(mTag).build();
    }
}
