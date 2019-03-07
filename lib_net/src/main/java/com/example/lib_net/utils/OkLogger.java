package com.example.lib_net.utils;

import android.util.Log;

/**
 * Created by wangjiao on 2019/3/5.
 * 日志工具类
 * 后期组件化优化 把这拆离
 */

public class OkLogger {
    public static  boolean misLogEnable = true;
    private static  String mTag ="mmc-chloe";

    public static void debug(boolean isLogEnable){
        debug(mTag,isLogEnable);
    }
    private static void debug(String tag,boolean isLogEnable){
        mTag = tag;
        misLogEnable = isLogEnable;
    }

    public static void v(String msg){
        v(mTag,msg);
    }

    public static void v(String mTag, String msg) {
        Log.v(mTag,msg);
    }
    public static void d(String msg) {
        d(mTag, msg);
    }

    public static void d(String tag, String msg) {
        if (misLogEnable) Log.d(tag, msg);
    }

    public static void i(String msg) {
        i(mTag, msg);
    }

    public static void i(String tag, String msg) {
        if (misLogEnable) Log.i(tag, msg);
    }

    public static void w(String msg) {
        w(mTag, msg);
    }

    public static void w(String tag, String msg) {
        if (misLogEnable) Log.w(tag, msg);
    }

    public static void e(String msg) {
        e(mTag, msg);
    }

    public static void e(String tag, String msg) {
        if (misLogEnable) Log.e(tag, msg);
    }

    public static void printStackTrace(Throwable e){
        if(misLogEnable && e!=null){
            e.printStackTrace();
        }
    }
}
