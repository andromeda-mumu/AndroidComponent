package com.example.lib_net.module;

import java.io.Serializable;

/**
 * Created by 16244 on 2019/3/9.
 * 没有实体类的数据。
 * 场景：
 *  一般就是一些命令，成功或失败。
 *  不会有返回数据。
 */

public class SimpleResponse implements Serializable {
    private static final long serialVersionUID = -8199771661692763232L;
    public int code;
    public String msg;

    public CommonReqpons toCommonReponse(){
        CommonReqpons commonReqpons= new CommonReqpons();
        commonReqpons.resultcode =code;
        commonReqpons.reason = msg;
        return commonReqpons;
    }
}
