package com.tony.coder.im.db.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nostra13.universalimageloader.utils.L;
import com.tony.coder.im.db.DBHelper;
import com.tony.coder.im.db.DBHelper.FavTable;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.sns.UserHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 17:50
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DatabaseUtil {

    private static final String TAG = "DatabaseUtil";

    private static DatabaseUtil instance;

    /**
     * 数据库帮助类 *
     */
    private DBHelper mDbHelper;

    /**
     * 初始化
     *
     * @param context
     */
    private DatabaseUtil(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public synchronized static DatabaseUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseUtil(context);
        }
        return instance;
    }

    /**
     * 销毁
     */
    public static void destory() {
        if (instance != null) {
            instance.onDestory();
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        instance = null;
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
    }


    public void deleteFav(DynamicWall wall) {
        Cursor cursor = null;
        String where = FavTable.USER_ID + " = '" + UserHelper.getUserId()
                + "' AND " + FavTable.OBJECT_ID + " = '" + wall.getObjectId() + "'";
        cursor = mDbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int isLove = cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE));
            if (isLove == 0) {
                mDbHelper.delete(DBHelper.TABLE_NAME, where, null);
            } else {
                ContentValues cv = new ContentValues();
                cv.put(FavTable.IS_FAV, 0);
                mDbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
            }
        }
        if (cursor != null) {
            cursor.close();
            mDbHelper.close();
        }
    }


    public boolean isLoved(DynamicWall wall) {
        Cursor cursor = null;
        String where = FavTable.USER_ID + " = '" + UserHelper.getUserId()
                + "' AND " + FavTable.OBJECT_ID + " = '" + wall.getObjectId() + "'";
        cursor = mDbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE)) == 1) {
                return true;
            }
        }
        return false;
    }

    public long insertFav(DynamicWall wall) {
        long uri = 0;
        Cursor cursor = null;
        String where = FavTable.USER_ID + " = '" + UserHelper.getUserId()
                + "' AND " + FavTable.OBJECT_ID + " = '" + wall.getObjectId() + "'";
        cursor = mDbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues conv = new ContentValues();
            conv.put(FavTable.IS_FAV, 1);
            conv.put(FavTable.IS_LOVE, 1);
            mDbHelper.update(DBHelper.TABLE_NAME, conv, where, null);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(FavTable.USER_ID, UserHelper.getUserId());
            cv.put(FavTable.OBJECT_ID, wall.getObjectId());
            cv.put(FavTable.IS_LOVE, wall.isMyLove() == true ? 1 : 0);
            cv.put(FavTable.IS_FAV, wall.isMyFav() == true ? 1 : 0);
            uri = mDbHelper.insert(DBHelper.TABLE_NAME, null, cv);
        }
        if (cursor != null) {
            cursor.close();
            mDbHelper.close();
        }
        return uri;
    }

//	    public int deleteFav(DianDi qy){
//	    	int row = 0;
//	    	String where = FavTable.USER_ID+" = "+qy.getAuthor().getObjectId()
//	    			+" AND "+FavTable.OBJECT_ID+" = "+qy.getObjectId();
//	    	row = dbHelper.delete(DBHelper.TABLE_NAME, where, null);
//	    	return row;
//	    }


    /**
     * 设置内容的收藏状态
     *
     * @param lists
     */
    public List<DynamicWall> setFav(List<DynamicWall> lists) {
        Cursor cursor = null;
        if (lists != null && lists.size() > 0) {
            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                DynamicWall content = (DynamicWall) iterator.next();
                String where = FavTable.USER_ID + " = '" + UserHelper.getUserId()//content.getAuthor().getObjectId()
                        + "' AND " + FavTable.OBJECT_ID + " = '" + content.getObjectId() + "'";
                cursor = mDbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(FavTable.IS_FAV)) == 1) {
                        content.setMyFav(true);
                    } else {
                        content.setMyFav(false);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE)) == 1) {
                        content.setMyLove(true);
                    } else {
                        content.setMyLove(false);
                    }
                }
                L.i(TAG, content.isMyFav() + ".." + content.isMyLove());
            }
        }
        if (cursor != null) {
            cursor.close();
            mDbHelper.close();
        }
        return lists;
    }

    /**
     * 设置内容的收藏状态
     *
     * @param lists
     */
    public List<DynamicWall> setFavInFav(List<DynamicWall> lists) {
        Cursor cursor = null;
        if (lists != null && lists.size() > 0) {
            for (Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                DynamicWall content = (DynamicWall) iterator.next();
                content.setMyFav(true);
                String where = FavTable.USER_ID + " = '" + UserHelper.getUserId()
                        + "' AND " + FavTable.OBJECT_ID + " = '" + content.getObjectId() + "'";
                cursor = mDbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE)) == 1) {
                        content.setMyLove(true);
                    } else {
                        content.setMyLove(false);
                    }
                }
                L.i(TAG, content.isMyFav() + ".." + content.isMyLove());
            }
        }
        if (cursor != null) {
            cursor.close();
            mDbHelper.close();
        }
        return lists;
    }


    public ArrayList<DynamicWall> queryFav() {
        ArrayList<DynamicWall> contents = null;
        // ContentResolver resolver = context.getContentResolver();
        Cursor cursor = mDbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        L.i(TAG, cursor.getCount() + "");
        if (cursor == null) {
            return null;
        }
        contents = new ArrayList<DynamicWall>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            DynamicWall content = new DynamicWall();
            content.setMyFav(cursor.getInt(3) == 1 ? true : false);
            content.setMyLove(cursor.getInt(4) == 1 ? true : false);
            L.i(TAG, cursor.getColumnIndex("isfav") + ".." + cursor.getColumnIndex("islove") + ".." + content.isMyFav() + "..." + content.isMyLove());
            contents.add(content);
        }
        if (cursor != null) {
            cursor.close();
        }
        // if (contents.size() > 0) {
        // return contents;
        // }
        return contents;
    }
}
