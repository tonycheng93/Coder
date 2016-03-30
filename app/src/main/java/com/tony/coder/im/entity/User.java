package com.tony.coder.im.entity;

import cn.bmob.im.bean.BmobChatUser;
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
    /**
     * 发布的博客列表
     */
    private BmobRelation blogs;
    /**
     * //显示数据拼音的首字母
     */
    private String sortLetters;

    /**
     * //性别-true-男
     */
    private Boolean sex;

    private Blog blog;

    /**
     * 地理坐标
     */
    private BmobGeoPoint location;//

    private Integer hight;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BmobRelation getBlogs() {
        return blogs;
    }

    public void setBlogs(BmobRelation blogs) {
        this.blogs = blogs;
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

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
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
}
