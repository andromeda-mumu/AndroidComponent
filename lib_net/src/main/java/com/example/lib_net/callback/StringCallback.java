package com.example.lib_net.callback;

import com.example.lib_net.convert.StringConvert;

import okhttp3.Response;

/**
 * Created by wangjiao on 2019/3/5.
 * 返回字符串类型数据
 */

public abstract class StringCallback extends AbstractCallback<String> {
    private StringConvert mConvert;
    public StringCallback(){
        mConvert = new StringConvert();
    }
    @Override
    public String convertResponse(Response response) {
       String s = mConvert.convertResponse(response);
       response.close();
       return s;
    }
}
