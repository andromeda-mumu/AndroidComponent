package com.example.lib_net.download;

import com.example.lib_net.task.XExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 16244 on 2019/3/10.
 * 下载管理的线程池
 */

public class DownloadThreadPool {
    public static final int MAX_POOL_SIZE= 5;//最大线程数
    public static final int KEEP_ALIVE_TIME = 1;//存活时间
    private static final TimeUnit UNIT = TimeUnit.HOURS;
    private int mCorePoolSize =3;//核心线程池的数量，同时能执行的线程数量 ，默认3个
    private XExecutor mExecutor; //线程池执行器

    public XExecutor getExecutor() {
        if(mExecutor==null){
            synchronized (DownloadThreadPool.class){
                mExecutor = new XExecutor(mCorePoolSize,MAX_POOL_SIZE,KEEP_ALIVE_TIME,UNIT,
                        new PriorityBlockingQueue<Runnable>(), //无限容量缓冲队列
                        Executors.defaultThreadFactory(), //线程创建工程
                        new ThreadPoolExecutor.AbortPolicy()); //继续超出上限的策略，阻止
            }
        }
        return mExecutor;
    }

    /** 必须在首次执行前设置，否者无效 ,范围1-5之间 */
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize <= 0) corePoolSize = 1;
        if (corePoolSize > MAX_POOL_SIZE) corePoolSize = MAX_POOL_SIZE;
        this.mCorePoolSize = corePoolSize;
    }

    /** 执行任务 */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            getExecutor().execute(runnable);
        }
    }

    /**移除线程*/
    public void removve(Runnable runnable){
        if(runnable!=null) {
            getExecutor().remove(runnable);
        }
    }
}
