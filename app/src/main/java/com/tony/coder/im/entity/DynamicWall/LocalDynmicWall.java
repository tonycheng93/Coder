package com.tony.coder.im.entity.DynamicWall;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 17:45
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@DatabaseTable(tableName = "dynamicwall")
public class LocalDynmicWall {

    public static String _ID = "_id";
    public static String USER_ID = "userId";
    public static String OBJECT_ID = "objectId";
    public static String IS_LOVE = "isLove";
    public static String IS_FAV = "isFav";

    @DatabaseField(useGetSet = true, generatedId = true)
    private int _id;
    @DatabaseField(useGetSet = true)
    private String userId;
    @DatabaseField(useGetSet = true)
    private String objectId;
    @DatabaseField(useGetSet = true, defaultValue = "false")
    private String isLove;
    @DatabaseField(useGetSet = true, defaultValue = "false")
    private String isFav;

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getIsLove() {
        return isLove;
    }

    public void setIsLove(String isLove) {
        this.isLove = isLove;
    }

    @Override
    public String toString() {
        return "LocalDiandi{" +
                "_id=" + _id +
                ", userId='" + userId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", isLove=" + isLove +
                ", isFav=" + isFav +
                '}';
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
