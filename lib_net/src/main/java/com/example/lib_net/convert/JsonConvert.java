package com.example.lib_net.convert;


import com.example.lib_net.module.CommonReqpons;
import com.example.lib_net.module.SimpleResponse;
import com.example.lib_net.utils.ConvertUtils;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 16244 on 2019/3/9.
 */

public class JsonConvert<T> implements Convert<T> {
    private Type mType;
    private Class<T> mClazz;
    public JsonConvert(Type type){
        this.mType = type;
    }
    public JsonConvert(Class<T> clazz){
        mClazz = clazz;
    }
    @Override
    public T convertResponse(Response response) throws Throwable {
        if(mType==null){
            if(mClazz==null){
                //自动解析父类泛型的真实类型
                Type genType = getClass().getGenericSuperclass();
                mType = ((ParameterizedType)genType).getActualTypeArguments()[0];
            }else{
                return parseClass(response,mClazz);
            }
        }
        if(mType instanceof ParameterizedType){
            return parseParameterizedType(response,(ParameterizedType)mType);
        }else if(mType instanceof Class){
            return parseClass(response, (Class<?>) mType);
        }else
            return parseType(response,mType);
    }

    private T parseType(Response response, Type mType) {
        if(mType==null) return null;
        ResponseBody body = response.body();
        if(body==null) return null;
        JsonReader reader = new JsonReader(body.charStream());
        //泛型格式  new JsonCallback<任意javabean>(this)
        T t = ConvertUtils.fromJson(reader,mType);
        response.close();
        return t;
    }

    private T parseParameterizedType(Response response, ParameterizedType mType) {
        if(mType==null) return null;
        ResponseBody body = response.body();
        if(body==null) return null;
        JsonReader reader = new JsonReader(body.charStream());

        Type rawType = mType.getRawType();//泛型的实际类型
        Type typeArgument = mType.getActualTypeArguments()[0];//泛型的参数
        if(rawType!= CommonReqpons.class){
            T t = ConvertUtils.fromJson(reader,mType);
            response.close();
            return t;
        }else{
            if(typeArgument==Void.class){
                // 泛型格式如下： new JsonCallback<CommonResponse<Void>>(this)
                SimpleResponse simpleResponse = ConvertUtils.fromJson(reader,SimpleResponse.class);
                response.close();
                return (T) simpleResponse.toCommonReponse();
            }else{
                // 泛型格式如下： new JsonCallback<CommonResponse<内层JavaBean>>(this)
                //这里是关键  解析泛型，主要是mtype是ParameterizedType类型的
                CommonReqpons<T> commonReqpons = ConvertUtils.fromJson(reader,mType);
                response.close();
                int code = commonReqpons.resultcode;
                //code==0成功 其他失败
                if(code==200){
                   return (T) commonReqpons;
                }else if(code == 104){
                    throw new IllegalStateException("用户授权失效");
                }else if(code==-1){
                    throw new IllegalStateException("用户账号在别处登录");
                }else if(code==-2){
                    throw new IllegalStateException("用户信息过期");
                }else {
                    throw new IllegalStateException("错误代码：" + code + "，错误信息：" + commonReqpons.reason);
                }

            }
        }


    }

    private T parseClass(Response response, Class<?> rawType) throws Exception{
        if(rawType==null) return null;
        ResponseBody body = response.body();
        if(body==null) return null;
        JsonReader reader = new JsonReader(body.charStream());
        if(rawType == String.class){
            return (T) body.string();
        }else if(rawType == JSONObject.class){
            return (T) new JSONObject(body.string());
        }else if(rawType == JSONArray.class){
            return (T) new JSONArray(body.string());
        }else{
            T t = ConvertUtils.fromJson(reader,rawType);
            response.close();
            return t;
        }

    }
}
