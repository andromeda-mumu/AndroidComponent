package com.example.lib_net.convert;

import okhttp3.Response;

/**
 * Created by wangjiao on 2019/3/5.
 * 网络数据接口转换
 */

public interface Convert<T> {
    /**
     * 因为异步请求，回调也在子线程
     * 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     * @param response
     * @return
     */
    T convertResponse(Response response) throws Throwable;
}
