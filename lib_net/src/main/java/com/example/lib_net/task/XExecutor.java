package com.example.lib_net.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 16244 on 2019/3/10.
 * 用于监听任务结束的回调
 */

public class XExecutor extends ThreadPoolExecutor{
    private Handler mInnerHandler = new Handler(Looper.getMainLooper());

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**任务结束时回调*/
    @Override
    protected void afterExecute(Runnable r, final Throwable t) {
        super.afterExecute(r, t);
        if(taskEndListeners!=null && taskEndListeners.size()>0){
            for(final OnTaskEndListener listener:taskEndListeners){
                mInnerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onTaskEnd(t);
                    }
                });
            }
        }
        //当前正在运行的数量为1，表示当前正在停止的任务，同时队列中没有任务，表示所有任务下载完毕
        if(getActiveCount()==1 && getQueue().size()==0){
            if(allTaskEndListeners!=null && allTaskEndListeners.size()>0){
                for (final OnAllTaskEndListener listener:allTaskEndListeners){
                    mInnerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAllTaskEnd();
                        }
                    });
                }
            }
        }



    }

    private List<OnTaskEndListener> taskEndListeners;
    public void addOnTaskEndListener(OnTaskEndListener listener){
        if(taskEndListeners==null)
            taskEndListeners = new ArrayList<>();
        taskEndListeners.add(listener);
    }
    public  void removeOnTaskEndListener(OnTaskEndListener listener){
        taskEndListeners.remove(listener);
    }

    public interface OnTaskEndListener{
        void onTaskEnd(Throwable t);
    }
    private List<OnAllTaskEndListener> allTaskEndListeners;
    public void addOnTaskEndListener(OnAllTaskEndListener listener){
        if(allTaskEndListeners==null)
            allTaskEndListeners= new ArrayList<>();
        allTaskEndListeners.add(listener);
    }
    public void removeTaskEndListener(OnAllTaskEndListener listener){
        allTaskEndListeners.remove(listener);
    }

    public interface OnAllTaskEndListener{
        void onAllTaskEnd();
    }

}
