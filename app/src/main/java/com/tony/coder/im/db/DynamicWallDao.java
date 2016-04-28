package com.tony.coder.im.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.orhanobut.logger.Logger;
import com.tony.coder.im.common.Constants;
import com.tony.coder.im.db.base.BaseDao;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.entity.DynamicWall.LocalDynmicWall;
import com.tony.coder.im.sns.UserHelper;

import java.sql.SQLException;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 17:59
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DynamicWallDao extends BaseDao<LocalDynmicWall> {

    private static final String TAG = "DynamicWallDao";
    private static DynamicWallDao mInstance;

    public DynamicWallDao(Context context) {
        super(context);
    }

    public static DynamicWallDao getInstance(Context context) {

        if (mInstance != null) {
            return mInstance;
        } else {
            return new DynamicWallDao(context);
        }
    }

    @Override
    public Dao<LocalDynmicWall, Integer> getDao() throws SQLException {
        if (mDao == null) {
            try {
                mDao = getHelper().getDao(LocalDynmicWall.class);
            } catch (SQLException e) {
                Logger.e(TAG, "得到Dao失败");
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isLoved(DynamicWall wall) {
        LocalDynmicWall localDynmicWall = null;
        try {
            localDynmicWall = queryByParams(new String[]{LocalDynmicWall.USER_ID,
                    LocalDynmicWall.OBJECT_ID}, new String[]{UserHelper.getUserId(), wall.getObjectId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return localDynmicWall.getIsLove().equals(Constants.TRUE);
    }

    public void deleteFav(DynamicWall wall) {
        LocalDynmicWall localDynmicWall = null;
        try {
            localDynmicWall = queryByParams(new String[]{LocalDynmicWall.USER_ID, LocalDynmicWall.OBJECT_ID},
                    new String[]{UserHelper.getUserId(), wall.getObjectId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        localDynmicWall.setIsFav(Constants.FALSE);
        create(localDynmicWall);
    }

    public void setFav(DynamicWall wall) {
        LocalDynmicWall localDynmicWall = null;
        localDynmicWall.setUserId(UserHelper.getUserId());
        localDynmicWall.setObjectId(wall.getObjectId());
        localDynmicWall.setIsFav(wall.isMyLove() == true ? Constants.TRUE : Constants.FALSE);
        localDynmicWall.setIsFav(wall.isMyFav() == true ? Constants.TRUE : Constants.FALSE);
        create(localDynmicWall);
    }
}
