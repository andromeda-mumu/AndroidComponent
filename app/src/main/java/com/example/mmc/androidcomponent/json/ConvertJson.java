package com.example.mmc.androidcomponent.json;

import com.example.lib_net.module.CommonReqpons;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 16244 on 2019/3/9.
 */

public class ConvertJson {
    public static <T> CommonReqpons<T> fromJsonObject(String json,Class clazz){
        Type type = new ParamterizedTypeImpl(CommonReqpons.class,new Class[]{clazz});
        return new Gson().fromJson(json,type);
    }

    public static <T> CommonReqpons<T> fromJsonArray(String json,Class<T> clazz){
        //生成list<T>中的list<T>
        Type listType = new ParamterizedTypeImpl(List.class,new Class[]{clazz});
        Type type = new ParamterizedTypeImpl(CommonReqpons.class,new Type[]{listType});
        return new Gson().fromJson(json,type);

    }
}
