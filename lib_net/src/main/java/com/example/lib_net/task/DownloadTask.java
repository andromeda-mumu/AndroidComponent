package com.example.lib_net.task;

import android.content.ContentValues;
import android.os.SystemClock;
import android.text.TextUtils;

import com.example.lib_net.OkDownload;
import com.example.lib_net.base.Request;
import com.example.lib_net.db.DownloadManager;
import com.example.lib_net.download.DownloadListener;
import com.example.lib_net.exception.HttpException;
import com.example.lib_net.exception.OkGoException;
import com.example.lib_net.exception.StorageException;
import com.example.lib_net.module.HttpHeader;
import com.example.lib_net.module.Progress;
import com.example.lib_net.module.Response;
import com.example.lib_net.utils.HttpUtils;
import com.example.lib_net.utils.IOUtils;
import com.example.lib_net.utils.OkLogger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.security.DomainCombiner;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.ResponseBody;

/**
 * Created by 16244 on 2019/3/10.
 *
 * 文件下载的任务类
 */

public class DownloadTask implements Runnable{
    public static final int BUFFER_SIZE =1024*8;
    public Progress mProgress;
    public Map<Object,DownloadListener> mListener;
    private ThreadPoolExecutor mExecutor;
    private PriorityRunnable mPriorityRunnable;
    public DownloadTask(String tag, Request<File,? extends Request> request){
        HttpUtils.checkNotNull(tag,"tag==null");
        mProgress = new Progress();

    }
    public void start(){
        if(OkDownload.getInstance().getTask(mProgress.tag)==null|| DownloadManager.getInstence().get(mProgress.tag)==null){
            throw new IllegalStateException("you must call DownloadTask#save() before DownloadTask#start()！");
        }
        if (mProgress.status == Progress.NONE || mProgress.status == Progress.PAUSE || mProgress.status == Progress.ERROR) {
            postOnStart(mProgress);
            postWaiting(mProgress);
            mPriorityRunnable = new PriorityRunnable(mProgress.priority,this);
            mExecutor.execute(mPriorityRunnable);
        }else if(mProgress.status == Progress.FINISH){
            if (mProgress.filePath == null) {
                postOnError(mProgress, new StorageException("the file of the task with tag:" + mProgress.tag + " may be invalid or damaged, please call the method restart() to download again！"));
            }else{
                File file = new File(mProgress.filePath);
                if(file.exists() && file.length()==mProgress.totalSize){
                    postFinish(mProgress,new File(mProgress.filePath));
                }else{
                    postOnError(mProgress, new StorageException("the file " + mProgress.filePath + " may be invalid or damaged, please call the method restart() to download again！"));
                }
            }
        }else {
            OkLogger.w("the task with tag " + mProgress.tag + " is already in the download queue, current task status is " + mProgress.status);
        }
    }

    private void postFinish(final Progress mProgress, final File file) {
        mProgress.speed =0;
        mProgress.fraction = 1.0f;
        mProgress.status = Progress.FINISH;
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                for (DownloadListener listener:mListener.values()){
                    listener.onProgress(mProgress);
                    listener.onFinish(file,mProgress);
                }
            }
        });
    }

    private void updateDatabase(Progress mProgress) {
        ContentValues contentValues = mProgress.buildContentValues(mProgress);
        DownloadManager.getInstence().update(contentValues,mProgress.tag);
    }

    private void postOnError(final Progress mProgress, Exception e) {
        mProgress.speed=0;
        mProgress.status = Progress.ERROR;
        mProgress.exception = e;
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener:mListener.values()){
                    listener.onProgress(mProgress);
                    listener.onError(mProgress);

                }
            }
        });


    }


    private void postWaiting(final Progress mProgress) {
        mProgress.speed = 0;
        mProgress.status = Progress.WAITING;
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : mListener.values()) {
                    listener.onProgress(mProgress);
                }
            }
        });
    }

    private void postOnStart(final Progress mProgress) {
        mProgress.speed = 0;
        mProgress.status = Progress.NONE;
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : mListener.values()) {
                    listener.onStart(mProgress);
                }
            }
        });
    }

    @Override
    public void run() {
        long startPos = mProgress.currentSize;
        if(startPos<0){
            postOnError(mProgress, OkGoException.BREAKPOINT_EXPIRED());
            return;
        }

        if(startPos>0){
            if(!TextUtils.isEmpty(mProgress.filePath)){
                File file = new File(mProgress.filePath);
                if(!file.exists()){
                    postOnError(mProgress,OkGoException.BREAKPOINT_NOT_EXIST());
                    return;
                }
            }
        }

        //request network from startPosition
        okhttp3.Response response = null;
        try{
            Request<?,? extends Request> request = mProgress.request;
            request.headers(HttpHeader.HEAD_KEY_RANGE,"bytes="+startPos+"-");
            response = request.execute();
        }catch (Exception e){
            postOnError(mProgress,e);
        }

        //check network data
        int code = response.code();
        if(code==404 || code>=500){
            postOnError(mProgress, HttpException.NET_ERROR());
        }
        ResponseBody body = response.body();
        if(body==null){
            postOnError(mProgress,new HttpException("response body is null"));
            return;
        }

        if(mProgress.totalSize==-1){
            mProgress.totalSize = body.contentLength();
        }
        //create filename
        String filename = mProgress.fileName;
        if(TextUtils.isEmpty(filename)){
            filename = HttpUtils.getNetFileName(response,mProgress.url);
            mProgress.fileName = filename;
        }
        if(!IOUtils.createFloder(mProgress.folder)){
            postOnError(mProgress, StorageException.NOT_AVAILABLE());
            return;
        }
        //create and check file
        File file;
        if(TextUtils.isEmpty(mProgress.filePath)){
            file = new File(mProgress.folder,filename);
            mProgress.filePath = file.getAbsolutePath();
        }else {
            file = new File(mProgress.filePath);
        }
        if (startPos > 0 && !file.exists()) {
            postOnError(mProgress, OkGoException.BREAKPOINT_EXPIRED());
            return;
        }
        if (startPos > mProgress.totalSize) {
            postOnError(mProgress, OkGoException.BREAKPOINT_EXPIRED());
            return;
        }
        if (startPos == 0 && file.exists()) {
            IOUtils.delFileOrFolder(file);
        }
        if (startPos == mProgress.totalSize && startPos > 0) {
            if (file.exists() && startPos == file.length()) {
                postFinish(mProgress, file);
                return;
            } else {
                postOnError(mProgress, OkGoException.BREAKPOINT_EXPIRED());
                return;
            }
        }
        //start downloading
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file,"rw");
            randomAccessFile.seek(startPos);
            mProgress.currentSize = startPos;
        } catch (Exception e) {
            e.printStackTrace();
            postOnError(mProgress,e);
            return;
        }
        try{
            DownloadManager.getInstence().replace(mProgress);
            download(body.byteStream(),randomAccessFile,mProgress);
        }catch (Exception e){
            postOnError(mProgress,e);
            return;
        }

        //check finish status
        if(mProgress.status==Progress.PAUSE){
            postPause(mProgress);
        }else if(mProgress.status==Progress.LOADING){
            if(file.length()==mProgress.totalSize){
                postOnFinish(mProgress,file);
            }else{
                postOnError(mProgress,OkGoException.BREAKPOINT_EXPIRED());
            }
        } else {
            postOnError(mProgress, OkGoException.UNKNOWN());
        }

    }

    private void postOnFinish(final Progress mProgress, final File file) {
        mProgress.speed = 0;
        mProgress.fraction = 1.0f;
        mProgress.status = Progress.FINISH;
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : mListener.values()) {
                    listener.onProgress(mProgress);
                    listener.onFinish(file, mProgress);
                }
            }
        });
    }

    private void download(InputStream inputStream, RandomAccessFile randomAccessFile, Progress progress) {
        if(inputStream==null && randomAccessFile==null)return;
        progress.status = Progress.LOADING;
        byte[] buffer=new byte[BUFFER_SIZE];
        BufferedInputStream bis = new BufferedInputStream(inputStream,BUFFER_SIZE);
        int len;
        try {
            while ((len = bis.read(buffer,0,BUFFER_SIZE))!=-1 && progress.status==Progress.LOADING) {
                randomAccessFile.write(buffer,0,len);
                Progress.changeProgress(progress, len, progress.totalSize, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        postLoading(progress);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postLoading(final Progress progress) {
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : mListener.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    public void restart(){
        pause();
        IOUtils.delFileOrFolder(mProgress.filePath);
        mProgress.status = Progress.NONE;
        mProgress.currentSize = 0;
        mProgress.fraction = 0;
        mProgress.speed = 0;
        DownloadManager.getInstence().replace(mProgress);
        start();
    }

    public void pause() {
        mExecutor.remove(mPriorityRunnable);
        if(mProgress.status==Progress.WAITING){
            postPause(mProgress);
        }else if(mProgress.status==Progress.LOADING){
            mProgress.speed=0;
            mProgress.status = Progress.PAUSE; //yyy 这里就算停止正在下载的任务了？？ 我猜是在下载任务那里有用到progress
        }else{
            OkLogger.w("only the task with status WAITING(1) or LOADING(2) can pause, current status is " + mProgress.status);
        }
    }

    private void postPause(final Progress mProgress) {
        mProgress.speed = 0;
        mProgress.status = Progress.PAUSE;
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : mListener.values()) {
                    listener.onProgress(mProgress);
                }
            }
        });
    }

    public void remove(){
        remove(false);
    }
    public DownloadTask remove(Boolean isDeleteFile) {
        pause();
        if(isDeleteFile) IOUtils.delFileOrFolder(mProgress.filePath);
        DownloadManager.getInstence().delete(mProgress.tag);
        DownloadTask task = OkDownload.getInstance().removeTask(mProgress.tag);
        postOnRemove(mProgress);
        return task;
    }

    private void postOnRemove(final Progress mProgress) {
        updateDatabase(mProgress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : mListener.values()) {
                    listener.onRemove(mProgress);
                }
                mListener.clear();
            }
        });
    }

    public DownloadTask register(DownloadListener listener) {
        if (listener != null) {
            mListener.put(listener.tag, listener);
        }
        return this;
    }

    public void unRegister(DownloadListener listener) {
        HttpUtils.checkNotNull(listener, "listener == null");
        mListener.remove(listener.tag);
    }
}
