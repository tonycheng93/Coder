package com.tony.coder.im.entity;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/30 15:26
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class User extends BmobChatUser {

    private static final long serialVersionUID = 1L;
    private BmobRelation favorite;
    private BmobFile avatarImg;
    private String signature;
    private String background;
    private String sortLetters;//显示数据拼音的首字母
    private Boolean sex;//性别-true-男
    private BmobGeoPoint location;//地理坐标
    private Integer hight;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }


    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public Integer getHight() {
        return hight;
    }

    public void setHight(Integer hight) {
        this.hight = hight;
    }

    public BmobRelation getFavorite() {
        return favorite;
    }

    public void setFavorite(BmobRelation favorite) {
        this.favorite = favorite;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public BmobFile getAvatarImg() {
        return avatarImg;
    }

    public void setAvatarImg(BmobFile avatarImg) {
        this.avatarImg = avatarImg;
    }
}
