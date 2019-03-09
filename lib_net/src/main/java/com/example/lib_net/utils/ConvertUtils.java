package com.example.lib_net.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.readystatesoftware.chuck.internal.support.JsonConvertor;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by 16244 on 2019/3/9.
 *  json转换的工具类
 */

public class ConvertUtils {
    private static Gson create(){
        return GsonHolder.gson;
    }
    private static class GsonHolder{
        private static Gson gson = new Gson();
    }

    public static <T> T fromJson(String json,Class<T> clazz){
        return create().fromJson(json,clazz);
    }
    public static <T> T fromJson(String json,Type type) throws JsonIOException, JsonSyntaxException{
        return create().fromJson(json,type);
    }
    public static <T> T fromJson(JsonReader reader, Type typeOfT) {
        return create().fromJson(reader,typeOfT);
    }
    public static <T> T fromJson(Reader reader,Class<T> clazz){
        return create().fromJson(reader,clazz);
    }
    public static <T> T fromJson(Reader reader,Type type){
        return create().fromJson(reader,type);
    }

    public static String toJson(Object src){
        return create().toJson(src);
    }

    public static String toJson(Object src,Type type){
        return create().toJson(src,type);
    }
    public static String formatJson(String json){
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);
        return JsonConvertor.getInstance().toJson(jsonElement);
    }
    public static String formatJson(Object src){
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(toJson(src));
        return JsonConvertor.getInstance().toJson(jsonElement);
    }
}
