package com.example.lib_net.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.lib_net.cahce.CacheEntity;

/**
 * Created by wangjiao on 2019/3/6.
 */

public class CacheManager extends BaseDao<CacheEntity<?>> {

    public static CacheManager getInstance(){
        return CacheManagerHolder.instance;
    }
    private static class CacheManagerHolder{
        private static CacheManager instance = new CacheManager();
    }
    /**--------------根据key获取缓存----------------*/
    public CacheEntity<?> get(String ket){
        //ttt
        return null;
    }



    private CacheManager() {
        super(new DbHelper());
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void unInit() {

    }

    @Override
    public CacheEntity<?> parseCursorToBean(Cursor cursor) {
        return null;
    }

    @Override
    public ContentValues getContentValues(CacheEntity<?> cacheEntity) {
        return null;
    }
}
