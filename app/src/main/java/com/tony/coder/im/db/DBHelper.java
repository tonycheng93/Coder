package com.tony.coder.im.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 16:23
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "dynamic_wall_db";
    public static final int DATA_BASE_VERSION = 1;
    public static final String TABLE_NAME = "fav";

    private SQLiteDatabase mDatabase;

    public DBHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreateFavTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void onCreateFavTable(SQLiteDatabase db) {
        StringBuilder favStr = new StringBuilder();
        favStr.append("CREATE TABLE IF NOT EXISTS ")
                .append(DBHelper.TABLE_NAME)
                .append(" ( ").append(FavTable._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(FavTable.USER_ID).append(" varchar(100),")
                .append(FavTable.OBJECT_ID).append(" varchar(20),")
                .append(FavTable.IS_FAV)
                .append(" Integer,")
                .append(FavTable.IS_LOVE)
                .append(" Integer);");
        db.execSQL(favStr.toString());
    }

    /**
     * 获取数据库操作对象
     *
     * @param isWrite
     * @return
     */
    public synchronized SQLiteDatabase getDatabase(boolean isWrite) {
        if (mDatabase == null || !mDatabase.isOpen()) {
            if (isWrite) {
                try {
                    mDatabase = getWritableDatabase();
                } catch (Exception e) {
                    //当数据库不可写时
                    mDatabase = getReadableDatabase();
                    return mDatabase;
                }
            } else {
                mDatabase = getReadableDatabase();
            }
        }
        return mDatabase;
    }

    public int delete(String table, String whereClaus, String[] whereArgs) {
        getDatabase(true);
        return mDatabase.delete(table, whereClaus, whereArgs);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        getDatabase(true);
        return mDatabase.insertOrThrow(table, nullColumnHack, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        getDatabase(true);
        return mDatabase.update(table, values, whereClause, whereArgs);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        getDatabase(false);
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    public void execSQL(String sql) {
        getDatabase(true);
        mDatabase.execSQL(sql);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        getDatabase(false);
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public interface FavTable {
        String _ID = "_id";
        String USER_ID = "userid";
        String OBJECT_ID = "objectid";
        String IS_LOVE = "islove";
        String IS_FAV = "isfav";
    }
}
