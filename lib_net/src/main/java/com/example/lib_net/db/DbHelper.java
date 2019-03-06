package com.example.lib_net.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lib_net.OkClient;
import com.example.lib_net.cahce.CacheEntity;
import com.example.lib_net.cookie.SerializableCookie;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangjiao on 2019/3/6.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_CACHE_NAME="mmc.db";
    public static final int DB_CACHE_VERSION=1;
    public static final String TABLE_CACHE ="cache";
    public static final String TABLE_COOKIE ="cookie";
    public static final String TABLE_DOWNLOAD ="download";
    public static final String TABLE_UPLOAD ="upload";

    static final Lock lock = new ReentrantLock(); // yyy 什么作用
    private TableEntity cacheTableEntity = new TableEntity(TABLE_CACHE);
    private TableEntity cookieTableEntity = new TableEntity(TABLE_COOKIE);
    private TableEntity downloadTableEntity = new TableEntity(TABLE_DOWNLOAD);
    private TableEntity uploadTableEntity = new TableEntity(TABLE_UPLOAD);

    public DbHelper(){
        this(OkClient.getInstance().getContext());
    }
    public DbHelper(Context context) {
        super(context, DB_CACHE_NAME, null, DB_CACHE_VERSION);

        cacheTableEntity.addColumn(new ColumnEntity(CacheEntity.KEY,"VARCHAR",true,true))
                .addColumn(new ColumnEntity(CacheEntity.LOCAL_EXPIRE,"INTEGER"))
                .addColumn(new ColumnEntity(CacheEntity.HEAD,"BLOB"))
                .addColumn(new ColumnEntity(CacheEntity.DATA,"BLOB"));

        cookieTableEntity.addColumn(new ColumnEntity(SerializableCookie.NAME,"VARCHAR"))
                .addColumn(new ColumnEntity(SerializableCookie.HOST,"VARCHAR"))
                .addColumn(new ColumnEntity(SerializableCookie.DOMAIN,"VARCHAR"))
                .addColumn(new ColumnEntity(SerializableCookie.COOKIE,"BLOB"))
                .addColumn(new ColumnEntity(SerializableCookie.DOMAIN,SerializableCookie.HOST,SerializableCookie.NAME));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
          db.execSQL(cacheTableEntity.buildTableString());
          db.execSQL(cookieTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(DbUtils.isNeedUpgradeTable(db,cacheTableEntity)) db.execSQL("DROP TABLE IF EXISTS "+TABLE_CACHE);
        if(DbUtils.isNeedUpgradeTable(db,cookieTableEntity)) db.execSQL("DROP TABLE IF EXISTS "+TABLE_COOKIE);
        onCreate(db);
    }
}
