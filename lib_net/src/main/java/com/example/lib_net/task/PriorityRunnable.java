package com.example.lib_net.task;

import java.util.PriorityQueue;

/**
 * Created by 16244 on 2019/3/10.
 * Runnable对象的优先级封装
 */

public class PriorityRunnable extends PriorityObject<Runnable> implements Runnable {
    public PriorityRunnable(int priority, Runnable obj) {
        super(priority, obj);
    }

    @Override
    public void run() {
        this.obj.run();
    }
}
