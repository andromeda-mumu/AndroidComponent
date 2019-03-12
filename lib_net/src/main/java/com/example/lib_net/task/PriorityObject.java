package com.example.lib_net.task;

/**
 * Created by 16244 on 2019/3/10.
 * 具有优先级对象的公共类
 */

public class PriorityObject<E> {
    private final  int priority;
    public final E obj;
    public PriorityObject(int priority,E obj){
        this.obj = obj;
        this.priority = priority;
    }
}
