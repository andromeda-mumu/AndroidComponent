package com.example.lib_net.download;

import android.os.RecoverySystem;

import com.example.lib_net.ProgressListener;

import java.io.File;

/**
 * Created by 16244 on 2019/3/10.
 * 全局的下载监听
 */

public abstract class DownloadListener implements ProgressListener<File>{
    public final Object tag;
    public DownloadListener(Object tag){this.tag = tag;}
}
