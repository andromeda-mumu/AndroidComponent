package com.example.lib_net.callback;

import com.example.lib_net.base.Request;
import com.example.lib_net.convert.JsonConvert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by wangjiao on 2019/3/5.
 * 默认将数据解析成javabean
 */

public abstract class JsonCallback<T> extends AbstractCallback<T> {

    private Type mType;
    private Class<T> mClazz;
    public JsonCallback(){}
    public JsonCallback(Type type){
        this.mType = type;
    }
    public JsonCallback(Class<T> clazz){
        mClazz = clazz;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        //用于所有请求前添加公共请求头或请求参数
        //如登录授权token  设备信息等
//        request.headers("header1","headervalue1")
//                .params("params1","paramvalue");
    }

    @Override
    public T convertResponse(Response response)throws Throwable {
       if(mType==null){
           if(mClazz==null){
               Type genType = getClass().getGenericSuperclass();
               mType = ((ParameterizedType)genType).getActualTypeArguments()[0];
           }else{
               JsonConvert<T> convert = new JsonConvert<T>(mClazz);
               return  convert.convertResponse(response);
           }
       }
       JsonConvert<T> jsonConvert = new JsonConvert<T>(mType);
       return jsonConvert.convertResponse(response);
    }


}
