package com.tony.coder.im.db.base;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.tony.coder.im.db.DataHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 17:34
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public abstract class BaseDao<T> {

    private final static String TAG = "MyDao";
    public Dao<T, Integer> mDao = null;
    private DataHelper mDataHelper;
    private Context mContext;

    public BaseDao(Context context) {
        this.mContext = context;
        getHelper();
        try {
            getDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataHelper getHelper() {
        if (mDataHelper == null) {
            mDataHelper = OpenHelperManager.getHelper(mContext, DataHelper.class);
        }
        return mDataHelper;
    }

    public abstract Dao<T, Integer> getDao() throws SQLException;

    public void create(T t) {
        try {
            mDao.create(t);
        } catch (SQLException e) {
            Log.e(TAG, "创建失败");
            e.printStackTrace();
        }
    }

    public void delete(T t) {
        try {
            mDao.delete(t);
        } catch (SQLException e) {
            Log.e(TAG, "删除失败");
            e.printStackTrace();
        }
    }

    public void deleteById(int id) {
        try {
            mDao.deleteById(id);
        } catch (SQLException e) {
            Log.e(TAG, "删除失败");
            e.printStackTrace();
        }
    }

    public List<T> queryTs() {
        List<T> list = new ArrayList<T>();
        try {
            list = mDao.queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "查询失败");
            e.printStackTrace();
        }
        return list;
    }

    public T queryTById(int id) {
        T t = null;
        try {
            t = mDao.queryForId(id);
        } catch (SQLException e) {
            Log.e(TAG, "查询失败");
            e.printStackTrace();
        }
        return t;
    }

    public void update(T t) {
        try {
            mDao.update(t);
        } catch (SQLException e) {
            Log.e(TAG, "更新失败");
            e.printStackTrace();
        }
    }

    public T queryByParam(String idName, String idValue) throws SQLException {
        List<T> lst = query(idName, idValue);
        if (null != lst && !lst.isEmpty()) {
            return lst.get(0);
        } else {
            return null;
        }
    }

    public T queryByParams(String[] attributeNames, String[] attributeValues) throws SQLException {
        List<T> lst = query(attributeNames, attributeValues);
        if (null != lst && !lst.isEmpty()) {
            return lst.get(0);
        }
        return null;
    }

    public List<T> query(PreparedQuery<T> preparedQuery) throws SQLException {
        Dao<T, Integer> dao = getDao();
        return dao.query(preparedQuery);
    }

    public List<T> query(String attributeName, String attributeValue) throws SQLException {
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
        queryBuilder.where().eq(attributeName, attributeValue);
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public List<T> query(String[] attributeNames, String[] attributeValues) throws SQLException {
        if (attributeNames.length != attributeValues.length) {
            Log.e(TAG, "params size is not equal");
        }
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
        Where<T, Integer> wheres = queryBuilder.where();
        for (int i = 0; i < attributeNames.length; i++) {
            wheres.eq(attributeNames[i], attributeValues[i]);
        }
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }
}
