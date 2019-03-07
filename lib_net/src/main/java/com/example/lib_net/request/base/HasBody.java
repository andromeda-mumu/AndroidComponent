package com.example.lib_net.request.base;

import com.example.lib_net.module.HttpParams;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wangjiao on 2019/3/7.
 *
 * 表示当前请求是否具有请求体
 */

public interface HasBody<R> {
    R isMultipart(boolean isMultipart);
    R params(String key,File file);
    R addFileParams(String key, List<File> files);
    R addFileWrapperParams(String key, List<HttpParams.FileWrapper> wrappers);
    R params(String key,File file,String filename);
    R params(String key, File file, String filename, MediaType contentType);
    R upString(String string);
    R upString(String string,MediaType mediaType);

    R upRequestBody(RequestBody requestBody);
    R upJson(String json);
    R upJson(JSONObject jsonObject);
    R upByte(byte[] bytes);
    R upFile(File file);
    R upFile(File file,MediaType mediaType);

}
