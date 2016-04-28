package com.tony.coder.im.db.base;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 16:39
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public abstract class BaseDaoT<T> {

    private static final String TAG = "BaseDaoT";
    protected DatabaseHelperT mDatabaseHelperT;
    protected Context mContext;

    public BaseDaoT(Context context) {
        mContext = context;
        getHelper();
    }

    public DatabaseHelperT getHelper() {
        if (mDatabaseHelperT == null) {
            mDatabaseHelperT = OpenHelperManager.getHelper(mContext, DatabaseHelperT.class);
        }
        return mDatabaseHelperT;
    }

    public abstract Dao<T, Integer> getDao() throws SQLException;

    public int save(T t) throws SQLException {
        return getDao().create(t);
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

    public List<T> queryAll() throws SQLException {
        // QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
        // PreparedQuery<T> preparedQuery = queryBuilder.prepare();
        // return query(preparedQuery);
        Dao<T, Integer> dao = getDao();
        return dao.queryForAll();
    }

    public T queryById(String idName, String idValue) throws SQLException {
        List<T> lst = query(idName, idValue);
        if (null != lst && !lst.isEmpty()) {
            return lst.get(0);
        } else {
            return null;
        }
    }

    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        Dao<T, Integer> dao = getDao();
        return dao.delete(preparedDelete);
    }

    public int delete(T t) throws SQLException {
        Dao<T, Integer> dao = getDao();
        return dao.delete(t);
    }

    public int delete(List<T> lst) throws SQLException {
        Dao<T, Integer> dao = getDao();
        return dao.delete(lst);
    }

    public int delete(String[] attributeNames, String[] attributeValues) throws SQLException {
        List<T> lst = query(attributeNames, attributeValues);
        if (null != lst && !lst.isEmpty()) {
            return delete(lst);
        }
        return 0;
    }

    public int deleteById(String idName, String idValue) throws SQLException {
        T t = queryById(idName, idValue);
        if (null != t) {
            return delete(t);
        }
        return 0;
    }

    public int update(T t) throws SQLException {
        Dao<T, Integer> dao = getDao();
        return dao.update(t);
    }

    public boolean isTableExsits() throws SQLException {
        return getDao().isTableExists();
    }

    public long countOf() throws SQLException {
        return getDao().countOf();
    }

    public List<T> query(Map<String, Object> map) throws SQLException {
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
        if (!map.isEmpty()) {
            Where<T, Integer> wheres = queryBuilder.where();
            Set<String> keys = map.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                if (i == 0) {
                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));
                } else {
                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));
                }
            }
        }
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public List<T> query(Map<String, Object> map, Map<String, Object> lowMap,
                         Map<String, Object> highMap) throws SQLException {
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
        Where<T, Integer> wheres = queryBuilder.where();
        if (!map.isEmpty()) {
            Set<String> keys = map.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                if (i == 0) {
                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));
                } else {
                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));
                }
            }
        }
        if (!lowMap.isEmpty()) {
            Set<String> keys = lowMap.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                if (map.isEmpty()) {
                    wheres.gt(keyss.get(i), lowMap.get(keyss.get(i)));
                } else {
                    wheres.and().gt(keyss.get(i), lowMap.get(keyss.get(i)));
                }
            }
        }

        if (!highMap.isEmpty()) {
            Set<String> keys = highMap.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                wheres.and().lt(keyss.get(i), highMap.get(keyss.get(i)));
            }
        }
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }
}
