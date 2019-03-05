package com.example.lib_net.request.base;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by wangjiao on 2019/3/5.
 * 包装请求体，处理进度
 */

public class ProgressRequestBody<T> extends RequestBody {
    private RequestBody mRequestBody;
    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

    }
}
