package com.example.lib_net;


import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.lib_net.cahce.CacheMode;
import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.HttpParams;
import com.example.lib_net.request.DeleteRequest;
import com.example.lib_net.request.GetRequest;
import com.example.lib_net.request.HeadRequest;
import com.example.lib_net.request.OptionRequest;
import com.example.lib_net.request.PatchRequest;
import com.example.lib_net.request.PostRequest;
import com.example.lib_net.request.PutRequest;
import com.example.lib_net.request.TraceRequest;
import com.example.lib_net.utils.HttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by wangjiao on 2019/3/5.
 * 封装okHttp，供任何人，任何app使用。
 * 思想抽离出来，别局限自己当前app
 */

public class OkClient {
    public static final long DEFAULT_MILLISECONDS = 60000;//默认超时时间 1分钟
    public static final long REFRESH_TIME = 300; //回调刷新时间

    private HttpParams mCommonParams;//全局公共参数
    private HttpHeader mCommonHeader; //全局公共header
    private CacheMode mCacheMode;           //全局缓存模式

    private Context mContext;

    private int mRetryCount ;// 重試次数
    private final OkHttpClient mOkHttpClient;
    //todo #01 缓存模式 & 时间

    private Handler mDelivery;//线程调度器

   /**--------------单例 client基本配置----------------*/
    private OkClient() {
        mDelivery = new Handler(Looper.getMainLooper());
        mRetryCount = 3;
        mCacheMode = CacheMode.NO_CACHE;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //todo #02 自定义拦截器 日志
        builder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(DEFAULT_MILLISECONDS,TimeUnit.MILLISECONDS);
        builder.readTimeout(DEFAULT_MILLISECONDS,TimeUnit.MILLISECONDS);

        //todo #03 sslParams 什么作用呢

        mOkHttpClient = builder.build();
    }

    public void init(Application context){
        this.mContext = context;
    }
    public Context getContext(){
        HttpUtils.checkNotNull(mContext,"please call okClient.init first in application");
        return mContext;
    }

    public static OkClient getInstance(){
        return OkClientHolder.client;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    private static class OkClientHolder{
        private static OkClient client = new OkClient();
    }
    public OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }

    /**--------------get请求----------------*/
    public static <T> GetRequest<T> get(String url){
        return new GetRequest<>(url);
    }
    public static <T> HeadRequest<T> head(String url){
        return new HeadRequest<>(url);
    }
    public static <T> TraceRequest<T> trace(String url){
        return new TraceRequest<>(url);
    }
    /**--------------post请求----------------*/
    public static <T> PostRequest<T> post(String url){
        return new PostRequest<>(url);
    }
    public static <T> PatchRequest<T> patch(String url){
        return new PatchRequest<>(url);
    }
    public static <T> OptionRequest<T> options(String url){
        return new OptionRequest<>(url);
    }
    public static <T> DeleteRequest<T> delete(String url){
        return new DeleteRequest<>(url);
    }
    public static <T> PutRequest<T> put(String url){
        return new PutRequest<>(url);
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    /** 获取全局的缓存模式 */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }
    /** 全局的缓存模式 */
    public OkClient setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }
    public void setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        mRetryCount = retryCount;
    }

    /**-------------配置公共参数-----#04------------*/
    public void addCommonParams(HttpParams params){
        if(mCommonParams == null)
            mCommonParams = new HttpParams();
        mCommonParams.put(params);
    }
    public HttpParams getCommonParams(){
        return mCommonParams;
    }

    /**--------------配置Header----------------*/
    public void addCommonHeaders(HttpHeader header){
        if(mCommonHeader==null)
            mCommonHeader = new HttpHeader();
        mCommonHeader.put(header);
    }
    public HttpHeader getCommonHeader(){
        return mCommonHeader;
    }

    /**--------------取消请求---------------*/
    //根据Tag取消某一个请求
    public static void cancelTag(OkHttpClient client,Object tag){
        if(client==null || tag== null)return;
        for (Call call:client.dispatcher().queuedCalls()){
            if(call.request().tag().equals(tag)){
                call.cancel();
            }
        }
        for (Call call:client.dispatcher().runningCalls()){
            if(call.request().tag().equals(tag)){
                call.cancel();
            }
        }
    }
    public  void cancleAll(){
        for (Call call:getOkHttpClient().dispatcher().queuedCalls()){
            call.cancel();
        }
        for (Call call:getOkHttpClient().dispatcher().runningCalls()){
            call.cancel();
        }
    }



}
