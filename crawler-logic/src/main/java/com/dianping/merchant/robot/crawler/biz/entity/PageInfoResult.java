package com.dianping.merchant.robot.crawler.biz.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 解析出来的
 * 
 * @author mobangwei
 * 
 */
public class PageInfoResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String title;

    private String tag;

    private String subTitle;

    private String content;

    private String source;

    private String author;

    private String date;

    private String thumbnail;

    private Map<String, Object> extras;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

}
