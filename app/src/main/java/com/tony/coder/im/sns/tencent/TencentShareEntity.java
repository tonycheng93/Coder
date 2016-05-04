package com.tony.coder.im.sns.tencent;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/29 15:27
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class TencentShareEntity {
    private String title;
    private String imgUrl;
    private String targetUrl;
    private String summary;
    private String comment;

    /**
     * 该方法是默认的分享参数
     */
    public TencentShareEntity() {
        this(TencentShareConstants.TITLE, TencentShareConstants.IMG_URL, TencentShareConstants.TARGET_URL,
                TencentShareConstants.SUMMARY, TencentShareConstants.COMMENT);
    }

    /**
     * 此方法用来动态设置分享参数
     *
     * @param title
     * @param comment
     * @param summary
     * @param imgUrl
     * @param targetUrl
     */
    public TencentShareEntity(String title, String imgUrl, String targetUrl, String summary, String comment) {
        this.title = title;
        this.comment = comment;
        this.summary = summary;
        this.imgUrl = imgUrl;
        this.targetUrl = targetUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
