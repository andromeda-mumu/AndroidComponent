package com.example.lib_net.request;

import com.example.lib_net.module.HttpMethod;
import com.example.lib_net.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 16244 on 2019/3/8.
 */

public class DeleteRequest<T> extends BodyRequest<T,DeleteRequest<T>> {
    public DeleteRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    protected Request generateRequest(RequestBody requestBody) {
        Request.Builder builder = generateRequestBuilder(requestBody);
        return builder.delete(requestBody).url(mUrl).tag(mTag).build();
    }
}
