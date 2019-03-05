package com.example.lib_net;

import com.example.lib_net.callback.StringCallback;
import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.Response;

/**
 * Created by wangjiao on 2019/3/5.
 */

public class Test {

    public void get2String(String url){
        OkClient.<String>get(url)
                .tag("01")
                .headers(HttpHeader.HEAD_KEY_USER_AGENT,"userAgent")
                .headers("header2","headerValue2")
                .params("params1","paramsValue1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //yyy 这应该是在子线程吧
                    }
                });

    }
    public void post2String(String url){

    }
}
