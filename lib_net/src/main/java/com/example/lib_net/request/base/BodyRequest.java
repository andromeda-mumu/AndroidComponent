package com.example.lib_net.request.base;

import android.text.TextUtils;

import com.example.lib_net.base.Request;
import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.HttpParams;
import com.example.lib_net.utils.HttpUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wangjiao on 2019/3/7.
 */

public abstract class BodyRequest<T,R extends BodyRequest> extends Request<T,R> implements HasBody<R> {

    private static final long serialVersionUID = -427877682973247958L;

    protected boolean isMultipart = false ;//是否强制使用multipart/form-data表单上传
    protected boolean isSpliceUrl = false;//是否拼接url参数
    private String url;


    private RequestBody mRequestBody;
    private String mContent; //上传的文本内容
    private transient MediaType mMediaType;//上传的MIME类型
    private byte[] mBytes;//上传的字节数据

    private transient File mFile ;//单纯的上传一个文件

    public BodyRequest(String url) {
        super(url);
    }

    @Override
    public R isMultipart(boolean isMultipart) {
        return null;
    }

    @Override
    public R params(String key, File file) {
        mHttpParams.put(key,file);
        return (R)this;
    }

    @Override
    public R addFileParams(String key, List<File> files) {
        mHttpParams.putFileParams(key,files);
        return (R)this;
    }

    @Override
    public R addFileWrapperParams(String key, List<HttpParams.FileWrapper> wrappers) {
        mHttpParams.putFileWrapperParams(key,wrappers);
        return (R)this;
    }

    @Override
    public R params(String key, File file, String filename) {
        mHttpParams.put(key,file,filename);
        return (R)this;
    }

    @Override
    public R params(String key, File file, String filename, MediaType contentType) {
        mHttpParams.put(key,file,filename,contentType);
        return (R)this;
    }

    @Override
    public R upRequestBody(RequestBody requestBody) {
        this.mRequestBody = requestBody;
        return (R)this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @Override
    public R upString(String string) {
        this.mContent = string;
        this.mMediaType = HttpParams.MEDIA_TYPE_PLAIN;
        return (R)this;
    }

    @Override
    public R upString(String string, MediaType mediaType) {
        this.mContent = string;
        this.mMediaType = mediaType;
        return (R)this;
    }

    @Override
    public R upJson(String json) {
        this.mContent = json;
        this.mMediaType = HttpParams.MEDIA_TYPE_JSON;
        return (R)this;
    }

    @Override
    public R upJson(JSONObject jsonObject) {
        this.mContent = jsonObject.toString();
        this.mMediaType = HttpParams.MEDIA_TYPE_JSON;
        return (R)this;
    }

    @Override
    public R upByte(byte[] bytes) {
        this.mBytes = bytes;
        this.mMediaType = HttpParams.MEDIA_TYPE_STREAM;
        return (R)this;
    }

    @Override
    public R upFile(File file) {
        this.mFile = file;
        this.mMediaType = HttpUtils.guessMimeType(file.getName());
        return (R)this;
    }

    @Override
    public R upFile(File file, MediaType mediaType) {
        this.mFile = file;
        this.mMediaType = mediaType;
        return (R)this;
    }

    @Override
    protected RequestBody generateRequestBody() {
        if(isSpliceUrl)
            url = HttpUtils.createUrlFromParams(mBaseUrl,mHttpParams.urlParamsMap);
        if(mRequestBody!=null) return mRequestBody;

        if(mContent!=null && mMediaType!=null) return  RequestBody.create(mMediaType,mContent);
        if(mFile!=null && mMediaType!=null) return RequestBody.create(mMediaType,mFile);
        if(mBytes!=null && mMediaType!=null) return RequestBody.create(mMediaType,mBytes);
        return HttpUtils.generateMultipartRequestBody(mHttpParams,isMultipart);
    }
    protected okhttp3.Request.Builder generateRequestBuilder(RequestBody requestBody){
        try {
            headers(HttpHeader.HEAD_KEY_CONTENT_LENGTH,String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        return HttpUtils.appendHeader(builder,mHttpHeader);
    }
    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        String mediaTypeString = (String) inputStream.readObject();
        if(!TextUtils.isEmpty(mediaTypeString)){
            mMediaType = MediaType.parse(mediaTypeString);
        }
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();
        outputStream.writeObject(mMediaType==null?"":mMediaType.toString());
    }
}
