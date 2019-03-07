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

    public boolean remove(String cacheKey) {
        if(cacheKey==null) return false;
        return delete(CacheEntity.KEY+"=?",new String[]{cacheKey});

    }

    /**
     * 更新缓存 有就替换 没有就创建
     * @param cacheKey
     * @param entity
     * @param <T>
     */
    public <T> CacheEntity<T> replace(String cacheKey, CacheEntity<T> entity) {
        entity.setKey(cacheKey);
        replace(entity);
        return entity;
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
        return CacheEntity.getContentValues(cacheEntity);
    }
}
