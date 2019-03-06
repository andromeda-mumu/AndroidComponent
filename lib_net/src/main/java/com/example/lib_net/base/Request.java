package com.example.lib_net.base;

import com.example.lib_net.OkClient;
import com.example.lib_net.adaper.CacheCall;
import com.example.lib_net.adaper.Call;
import com.example.lib_net.cahce.CacheMode;
import com.example.lib_net.callback.Callback;
import com.example.lib_net.convert.Convert;
import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.HttpMethod;
import com.example.lib_net.module.HttpParams;
import com.example.lib_net.utils.HttpUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wangjiao on 2019/3/5.
 * 所有请求的基类，R作为request的子类，为了实现链式调用
 */

public abstract class Request<T,R extends Request> implements Serializable{
    private static final long serialVersionUID = 8883903509198350547L;

    protected String mUrl;
    protected String mBaseUrl;
    protected transient OkHttpClient mOkHttpClient;
    protected transient Object mTag;
    protected HttpHeader mHttpHeader = new HttpHeader();
    protected HttpParams mHttpParams = new HttpParams();

    protected int mRetryCount;
    private okhttp3.Request mRequest;
    private Callback<T> mCallback;
    private Call<T> mCall;

    private CacheMode mCacheMode;
    private long cacheTime;
    protected String cacheKey;
    protected transient Convert<T> converter;
    protected transient Callback<T> Callback;


    public Request(String url) {
        this.mUrl = url;
        mBaseUrl = url;
        OkClient okClient =OkClient.getInstance();
        //添加公共请求参数
        if(okClient.getCommonHeader()!=null)
            headers(okClient.getCommonHeader());
        if(okClient.getCommonParams()!=null)
            params(okClient.getCommonParams());

    }
    public R headers(HttpHeader header){
        mHttpHeader.put(header);
        return (R)this;
    }
    public R headers(String key,String value){
        mHttpHeader.put(key,value);
        return (R)this;
    }
    public R params(HttpParams params){
        mHttpParams.put(params);
        return (R)this;
    }

    public R tag(Object tag){
        this.mTag = tag;
        return (R)this;
    }
    public R retryCount(int retryCount){
        this.mRetryCount = retryCount;
        return (R)this;
    }
    public int getRetryCount(){
        return mRetryCount;
    }
    public R params(Map<String, String> params, boolean... isReplace) {
        this.mHttpParams.put(params, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }
    public R params(String key, String value) {
        mHttpParams.put(key, value,false);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, int value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, float value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, double value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, long value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, char value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, boolean value, boolean... isReplace) {
        mHttpParams.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addUrlParams(String key, List<String> values) {
        mHttpParams.putUrlParams(key, values);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeParam(String key) {
        mHttpParams.remove(key);
        return (R) this;
    }
    public abstract HttpMethod getMethod();
    /**--------------根据不同请求方式和参数，生成不同responseBody----------------*/
    protected abstract RequestBody generateRequestBody();

    /**--------------将requestBody转成request----------------*/
    protected abstract okhttp3.Request generateRequest(RequestBody requestBody);

    /**--------------构建请求体，返回call对象----------------*/
    public okhttp3.Call getRawCall(){
        RequestBody requestBody = generateRequestBody();
        if(requestBody!=null){
            //todo 包装一层 处理进度等
//            ProgressRequestBody<T> progressRequestBody = new ProgressRequestBody<>(requestBody, callback);
//            progressRequestBody.setInterceptor(uploadInterceptor);
            mRequest = generateRequest(requestBody);
        }else{
            mRequest = generateRequest(null);
        }
        if(mOkHttpClient==null)
            mOkHttpClient = OkClient.getInstance().getOkHttpClient();
        return mOkHttpClient.newCall(mRequest);
    }
    /**----Rx支持----------yyy 获取同步call对象----------------*/
    public Call<T> adapt(){
        if(mCall==null)
            mCall = new CacheCall<>(this);
        return mCall;
    }

    /**--------------同步请求--阻塞方法--------------*/
    public Response execute() throws IOException {
        return getRawCall().execute();
    }
    /**--------------异步，回调在子线程----------------*/
    public void execute(Callback<T> callback){
        HttpUtils.checkNotNull(callback,"callback==null");

        this.mCallback = callback;
        Call<T> call = adapt();
        call.execute(callback);
    }


    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    public R cacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return (R)this;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public R cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (R)this;
    }
    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public HttpParams getHttpParams() {
        return mHttpParams;
    }

    public void setHttpParams(HttpParams httpParams) {
        mHttpParams = httpParams;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public Convert<T> getConverter(){
        //convert优先级高于callback
        if(converter==null) converter=Callback;
        HttpUtils.checkNotNull(converter,"converter == null, do you forget to call Request#converter(Converter<T>) ?");
        return converter;
    }
}
