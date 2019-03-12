package com.example.lib_net;

import com.example.lib_net.module.Progress;

/**
 * Created by 16244 on 2019/3/10.
 */

public interface ProgressListener<T> {
    void onStart(Progress progress);
    void onProgress(Progress progress);
    void onError(Progress progress);
    void onFinish(T t,Progress progress);
    void onRemove(Progress progress);
}
