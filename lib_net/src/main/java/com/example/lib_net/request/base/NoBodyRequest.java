package com.example.lib_net.request.base;

import com.example.lib_net.base.Request;
import com.example.lib_net.utils.HttpUtils;

import okhttp3.RequestBody;

/**
 * Created by wangjiao on 2019/3/5.
 */

public abstract class NoBodyRequest<T,R extends Request> extends Request<T,R> {

    private static final long serialVersionUID = 8682024659457149958L;

    public NoBodyRequest(String url){
        super(url);
    }

    @Override
    protected RequestBody generateRequestBody() {
        return null;
    }
    protected okhttp3.Request.Builder generateRequestBuilder(RequestBody requestBody){
        mUrl = HttpUtils.createUrlFromParams(mBaseUrl,mHttpParams.urlParamsMap);
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        return HttpUtils.appendHeader(builder,mHttpHeader);
    }
}
