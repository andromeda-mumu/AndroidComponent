package com.example.lib_net.cahce;

import android.content.ContentValues;

import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.utils.IOUtils;

import java.io.Serializable;

/**
 * Created by wangjiao on 2019/3/6.
 */

public class CacheEntity<T> implements Serializable {
    private static final long serialVersionUID = 3526045551253485506L;
    /**--------------表中的字段----------------*/
    public static final String KEY ="key";
    public static final String LOCAL_EXPIRE="local_expire";
    public static final String HEAD ="head";
    public static final String DATA="data";


    public static final long CACHE_NEVER_EXPIRE =-1 ;//缓存永不过期
    private String key;
    private long localExpire; //缓存过期时间
    private HttpHeader responseHeader;//缓存的响应头
    private T data;//缓存实体
    private boolean isExpire;//缓存是否过期  不必存数据库，冬天计算

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLocalExpire() {
        return localExpire;
    }

    public void setLocalExpire(long localExpire) {
        this.localExpire = localExpire;
    }

    public HttpHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(HttpHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    /**--------------检查是否过期----------------*/
    /**
     *
     * @param cacheMode 缓存模式
     * @param cacheTime 允许缓存的时间
     * @param baseTime 基准时间，小于当前时间，视为过期
     * @return 是否过期
     * yyy  过期这个时间怎么算
     */
    public   boolean checkExpire(CacheMode cacheMode,long cacheTime,long baseTime){
        if(cacheTime==CACHE_NEVER_EXPIRE) return false;
        //304默认缓存模式，设置缓存时间无效，需要依靠服务器的响应头设置
        if(cacheMode==CacheMode.DEFAULT) return getLocalExpire()<baseTime;
        return getLocalExpire()+cacheTime<baseTime;
    }
    @Override
    public String toString() {
        return "CacheEntity{key='" + key + '\'' + //
                ", responseHeaders=" + responseHeader + //
                ", data=" + data + //
                ", localExpire=" + localExpire + //
                '}';
    }

    public static ContentValues getContentValues(CacheEntity<?> cacheEntity) {
        ContentValues values = new ContentValues();
        values.put(CacheEntity.KEY,cacheEntity.getKey());
        values.put(LOCAL_EXPIRE, cacheEntity.getLocalExpire());
        values.put(HEAD, IOUtils.toByteArray(cacheEntity.getResponseHeader()));
        values.put(DATA, IOUtils.toByteArray(cacheEntity.getData()));
        return values;
    }
}
