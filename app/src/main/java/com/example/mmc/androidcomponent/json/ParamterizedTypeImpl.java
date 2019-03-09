package com.example.mmc.androidcomponent.json;

import java.lang.reflect.*;

/**
 * Created by 16244 on 2019/3/9.
 *
 * gson解析泛型数据 result<T>
 */

public class ParamterizedTypeImpl implements java.lang.reflect.ParameterizedType {
    private final Class raw;
    private final Type[] args;
    public ParamterizedTypeImpl(Class raw,Type[] args){
        this.raw = raw;
        this.args=args!=null?args:new Type[0];
    }

    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
