package com.example.lib_net.module;

import java.io.Serializable;

/**
 * Created by 16244 on 2019/3/9.
 */

public class CommonReqpons<T> implements Serializable{
    private static final long serialVersionUID = -4960714043953757468L;
    public int resultcode;
    public String reason;
    public T result;
    @Override
    public String toString() {
        return "CommonResponse{\n" +//
                "\tcode=" + resultcode + "\n" +//
                "\tmsg='" + reason + "\'\n" +//
                "\tdata=" + result + "\n" +//
                '}';
    }
}
