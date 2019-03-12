package com.example.lib_net;

import android.os.Environment;

import com.example.lib_net.base.Request;
import com.example.lib_net.download.DownloadListener;
import com.example.lib_net.download.DownloadThreadPool;
import com.example.lib_net.module.Progress;
import com.example.lib_net.task.DownloadTask;
import com.example.lib_net.utils.IOUtils;
import com.example.lib_net.utils.OkLogger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 16244 on 2019/3/10.
 * 全局下载管理类
 */

public class OkDownload {
    private String folder; //下载的默认文件夹
    private DownloadThreadPool mThreadPool;// 下载的线程池
    private ConcurrentHashMap<String,DownloadTask> mTaskMap; //所有任务

    public static OkDownload getInstance(){
        return OkDownloadHolder.okDownload;
    }

    public DownloadTask getTask(String tag) {
        return mTaskMap.get(tag);
    }

    public DownloadTask removeTask(String tag) {
        return mTaskMap.remove(tag);
    }

    private static class OkDownloadHolder{
        private static final  OkDownload okDownload = new OkDownload();
    }
    private OkDownload(){
        folder = Environment.getExternalStorageDirectory()+ File.separator+"download"+File.separator;
        IOUtils.createFloder(folder);
        mThreadPool = new DownloadThreadPool();
        mTaskMap = new ConcurrentHashMap<>();

        //todo 校验数据的有效性

    }

    public static DownloadTask request(String tag, Request<File ,? extends Request> request){
        Map<String,DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        DownloadTask task = taskMap.get(tag);
        if(task == null){
            task = new DownloadTask(tag,request);
            taskMap.put(tag,task);
        }
        return task;
    }

    /**开始所有任务*/
    public void startAll(){
        for(Map.Entry<String,DownloadTask> entry:mTaskMap.entrySet()){
            DownloadTask task= entry.getValue();
            if(task==null){
                OkLogger.w("can't find task with tag =="+entry.getKey());
            }
            task.start();
        }
    }

    /**暂停全部任务*/
    public void pauseAll(){
        //先暂停未开始的
        for(Map.Entry<String,DownloadTask> entry:mTaskMap.entrySet()){
            DownloadTask task = entry.getValue();
            if(task==null){
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if(task.mProgress.status!= Progress.LOADING){
                task.pause();
            }
        }
        //在暂停正在进行的
        for(Map.Entry<String,DownloadTask> entry:mTaskMap.entrySet()){
            DownloadTask task = entry.getValue();
            if(task==null){
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if(task.mProgress.status== Progress.LOADING){
                task.pause();
            }
        }
    }
    /**删除所有任务*/
    public void removeAll(){
        removeAll(false);
    }

    /**
     *
     * @param isDeleteFile 删除任务是否删除文件
     */
    public void removeAll(Boolean isDeleteFile){
        //先删除未开始的任务
        for(Map.Entry<String,DownloadTask> entry:mTaskMap.entrySet()){
            DownloadTask task = entry.getValue();
            if(task==null){
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if(task.mProgress.status!= Progress.LOADING){
                task.remove(isDeleteFile);
            }
        }
        //再删除正在执行的任务
        for (Map.Entry<String, DownloadTask> entry : mTaskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.mProgress.status == Progress.LOADING) {
                task.remove(isDeleteFile);
            }
        }
    }
    private Map<String,DownloadTask> getTaskMap() {
        return mTaskMap;
    }

}
