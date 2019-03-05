package com.example.lib_net.module;

import com.example.lib_net.utils.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;

/**
 * #05
 * Created by wangjiao on 2019/3/5.
 * 自定义请求参数的实体类
 *
 * mmc:请求参数，类似hashmap。想不到其他了
 * -----想不到的地方-----
 * media的类型，也就是请求参数是有json,string,stream这几种类型
 * 普通键值对，文件的键值对
 *
 */
public class HttpParams implements Serializable{
    private static final long serialVersionUID = 870079269203578817L;

    public static final MediaType MEDIA_TYPE_PLAIN =MediaType.parse("text/plain;chatset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON =MediaType.parse("application/json;chatset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM =MediaType.parse("application/octet-stream");


    public LinkedHashMap<String,List<String>> urlParamsMap;
    public LinkedHashMap<String,List<FileWrapper>> fileParamsMap;

    public static final boolean IS_REPLACE = true;

    public HttpParams() {
        init();
    }

    private void init() {
        urlParamsMap = new LinkedHashMap<>();
        fileParamsMap = new LinkedHashMap<>();
    }

    public void put(HttpParams params){
        if(params!=null){
            if(params.urlParamsMap!=null && !params.urlParamsMap.isEmpty()) urlParamsMap.putAll(params.urlParamsMap);
            if(params.fileParamsMap!=null && !params.fileParamsMap.isEmpty()) fileParamsMap.putAll(params.fileParamsMap);
        }
    }
    public void put(Map<String,String> params,boolean ... isReplace){
        if(params==null || params.isEmpty()) return;
        for (Map.Entry<String,String> entry:params.entrySet()){
            put(entry.getKey(),entry.getValue(),isReplace);
        }
    }
    public void put(String key,String value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,value,isReplace[0]);
        }else{
            put(key,value,IS_REPLACE);//#07 什么作用
        }
    }

    public void put(String key,int value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,String.valueOf(value),isReplace[0]);
        }else{
            put(key,String.valueOf(value),IS_REPLACE);
        }
    }
    public void put(String key,long value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,String.valueOf(value),isReplace[0]);
        }else{
            put(key,String.valueOf(value),IS_REPLACE);
        }
    }
    public void put(String key,float value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,String.valueOf(value),isReplace[0]);
        }else{
            put(key,String.valueOf(value),IS_REPLACE);
        }
    }
    public void put(String key,double value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,String.valueOf(value),isReplace[0]);
        }else{
            put(key,String.valueOf(value),IS_REPLACE);
        }
    }
    public void put(String key,char value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,String.valueOf(value),isReplace[0]);
        }else{
            put(key,String.valueOf(value),IS_REPLACE);
        }
    }
    public void put(String key,boolean value,boolean... isReplace){
        if(isReplace!=null && isReplace.length>0){
            put(key,String.valueOf(value),isReplace[0]);
        }else{
            put(key,String.valueOf(value),IS_REPLACE);
        }
    }

    /**
     *
     * @param key
     * @param value
     * @param isReplace  是否替换掉原有的value.
     *
     *  #08 疑问：为啥一个key能对应那么多value,什么应用场景
     *
     */
    public void put(String key,String value ,boolean isReplace){
        if(key!=null && value!=null){
            List<String> urlValues = urlParamsMap.get(key);
            if(urlValues==null){
                urlValues = new ArrayList<>();
                urlParamsMap.put(key,urlValues);
            }
            if(isReplace)urlValues.clear();
            urlValues.add(value);
        }
    }
    public void putUrlParams(String key,List<String> values){
        if(key!=null && values!=null && !values.isEmpty()){
//            urlParamsMap.put(key,values);// mmcwhy 为什么不这样写，直接put进list
            for (String value:values){
                put(key,value,false);
            }
        }
    }

    public void put(String key,File file){
        put(key,file,file.getName());
    }
    public void put(String key,File file,String name){
        put(key,file,name, HttpUtils.guessMimeType(name));
    }
    public void put(String key, FileWrapper wrapper){
        put(key,wrapper.file,wrapper.filename,wrapper.contentType);
    }
    public void put(String key,File file,String fileName,MediaType mediaType){
        if(key!=null){
            List<FileWrapper> wrappers = fileParamsMap.get(key);
            if(wrappers==null){
                wrappers = new ArrayList<>();
                fileParamsMap.put(key,wrappers);
            }
            wrappers.add(new FileWrapper(file,fileName,mediaType));
        }
    }
    public void removeUrl(String key){
        urlParamsMap.remove(key);
    }
    public void removeFile(String key){
        urlParamsMap.remove(key);
    }

    public void remove(String key) {
        removeUrl(key);
        removeFile(key);
    }

    /**--------------#06-不知道为啥这样写---------------*/
    public static class FileWrapper implements Serializable{

        private static final long serialVersionUID = -6070757282275642384L;
        public File file;
        public String filename;
        public long filesize;
        public transient MediaType contentType;

        public FileWrapper(File file, String filename, MediaType contentType) {
            this.file = file;
            this.filename = filename;
            this.contentType = contentType;
            this.filesize = file.length();
        }
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(contentType.toString());
        }
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            contentType = MediaType.parse((String)in.readObject());
        }

        @Override
        public String toString() {
            return "FileWrapper{" + //
                    "file=" + file + //
                    ", fileName=" +filename + //
                    ", contentType=" + contentType + //
                    ", fileSize=" + filesize +//
                    "}";
        }
    }
}
