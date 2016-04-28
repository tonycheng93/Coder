package com.tony.coder.im.entity.DynamicWall;

import com.tony.coder.im.entity.User;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/27 17:54
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DynamicWall extends BmobObject implements Serializable {
    /**
     * 社区动态的每个item
     */
    protected User author;
    private String content;
    private BmobFile contentfigureurl;
    private int love;
    private int hate;
    private int share;
    private int comment;
    private boolean isPass;
    private boolean myFav;//
    private boolean myLove;//赞
    private BmobRelation relation;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getContentfigureurl() {
        return contentfigureurl;
    }

    public void setContentfigureurl(BmobFile contentfigureurl) {
        this.contentfigureurl = contentfigureurl;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public int getHate() {
        return hate;
    }

    public void setHate(int hate) {
        this.hate = hate;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public boolean isMyFav() {
        return myFav;
    }

    public void setMyFav(boolean myFav) {
        this.myFav = myFav;
    }

    public boolean isMyLove() {
        return myLove;
    }

    public void setMyLove(boolean myLove) {
        this.myLove = myLove;
    }

    public BmobRelation getRelation() {
        return relation;
    }

    public void setRelation(BmobRelation relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "DynamicWall{" +
                "author=" + author +
                ", content='" + content + '\'' +
                ", contentfigureurl=" + contentfigureurl +
                ", love=" + love +
                ", hate=" + hate +
                ", share=" + share +
                ", comment=" + comment +
                ", isPass=" + isPass +
                ", myFav=" + myFav +
                ", myLove=" + myLove +
                ", relation=" + relation +
                '}';
    }
}
