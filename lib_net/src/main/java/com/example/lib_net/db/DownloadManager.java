package com.example.lib_net.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lib_net.module.Progress;

/**
 * Created by 16244 on 2019/3/10.
 */

public class DownloadManager extends BaseDao<Progress> {
    private DownloadManager() {
        super(new DbHelper());
    }

    public static DownloadManager getInstence(){
        return DownloadManagerHolder.manager;
    }


    public boolean update(ContentValues contentValues, String tag) {
        return update(contentValues, Progress.TAG + "=?", new String[]{tag});
    }

    public void delete(String tag) {
        delete(Progress.TAG + "=?", new String[]{tag});
    }

    private static class DownloadManagerHolder{
        private static DownloadManager manager = new DownloadManager();
    }

    /** 获取下载任务 */
    public Progress get(String tag) {
        return queryOne(Progress.TAG + "=?", new String[]{tag});
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_DOWNLOAD;
    }

    @Override
    public void unInit() {

    }

    @Override
    public Progress parseCursorToBean(Cursor cursor) {
        return Progress.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(Progress progress) {
        return Progress.buildContentValues(progress);
    }
}
