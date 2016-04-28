package com.tony.coder.im.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orhanobut.logger.Logger;
import com.tencent.connect.UserInfo;

import java.io.File;
import java.sql.SQLException;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 16:41
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DatabaseHelperT extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "DatabaseHelperT";

    // name of the database file for your application -- change to something
    // appropriate for your app
    private static final String DATABASE_NAME = "dynamicwalls.db";

    // any time you make changes to your database objects, you may have to
    // increase the database version
    private static final int DATABASE_VERSION = 1;

    //数据库默认路径SDCard
    private static String DATABASE_PATH = Environment.getExternalStorageDirectory()
            + "/HAPPYCHAT.db";
    private static String DATABASE_PATH_JOURNAL = Environment.getExternalStorageDirectory()
            + "HAPPYCHAT.db-journal";

    private Context mContext;

    public DatabaseHelperT(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        initDatabasePath();
        try {
            File file = new File(DATABASE_PATH);
            if (!file.exists()) {
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH, null);
                onCreate(db);
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果没有SDCard默认存储在项目文件目录下
     */
    private void initDatabasePath() {
        DATABASE_PATH = mContext.getFilesDir().getAbsolutePath() + "/HAPPYCHAT.db";
        DATABASE_PATH_JOURNAL = mContext.getFilesDir().getAbsolutePath() + "/HAPPYCHAT.db-journal";
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Logger.d(DatabaseHelperT.class.getName(), "onCreate");
        try {
            TableUtils.clearTable(connectionSource, UserInfo.class);
        } catch (SQLException e) {
            Logger.e(DatabaseHelperT.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Logger.d(DatabaseHelperT.class.getName(), "onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, UserInfo.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Logger.e(DatabaseHelperT.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteDB() {
        if (mContext != null) {
            File file = mContext.getDatabasePath(DATABASE_NAME);
            if (file.exists()) {
                Logger.d("DB", "---delete SDCard DB---");
                file.delete();
            } else {
                Logger.d("DB", "---delete App DB---");
                mContext.deleteDatabase(DATABASE_NAME);
            }

            File mFile = mContext.getDatabasePath(DATABASE_NAME);
            if (mFile.exists()) {
                Logger.d("DB", "---delete SDCard DB 222---");
                mFile.delete();
            }

            File file1 = mContext.getDatabasePath(DATABASE_NAME);
            if (file1.exists()) {
                Logger.d("DB", "---delete SDCard DB 333---");
                file1.delete();
            }
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }
}
