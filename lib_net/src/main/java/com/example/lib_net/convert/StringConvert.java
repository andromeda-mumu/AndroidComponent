package com.example.lib_net.convert;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wangjiao on 2019/3/5.
 *  字符串转换器
 */

public class StringConvert implements Convert<String> {
    @Override
    public String convertResponse(Response response)throws Throwable {

        ResponseBody body = response.body();
        if(body==null)
            return null;
        return body.string();
    }
}
