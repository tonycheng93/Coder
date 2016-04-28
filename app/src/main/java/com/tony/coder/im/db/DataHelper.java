package com.tony.coder.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orhanobut.logger.Logger;
import com.tony.coder.im.entity.DynamicWall.LocalDynmicWall;
import com.tony.coder.im.sns.UserHelper;

import java.sql.SQLException;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 17:41
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DataHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = UserHelper.getUserId() + "_dynamicwall.db";
    private static final int DATABASE_VERSION = 1;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.clearTable(connectionSource, LocalDynmicWall.class);
        } catch (SQLException e) {
            Logger.e(DataHelper.class.getName(),"创建数据库失败",e);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
