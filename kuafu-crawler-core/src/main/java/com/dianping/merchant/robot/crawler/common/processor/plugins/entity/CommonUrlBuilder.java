package com.dianping.merchant.robot.crawler.common.processor.plugins.entity;

public class CommonUrlBuilder extends IUrlBuilder {

    /**
     * 
     */
    private static final long serialVersionUID = 9179410274578657081L;
    /**
     * 当前地址
     */
    public String currentUrl;
    /**
     * 当前爬取深度
     */
    public Integer currentDeep;

    /**
     * url计数器
     */
    public Integer currentCount;


    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public Integer getCurrentDeep() {
        return currentDeep;
    }

    public void setCurrentDeep(Integer currentDeep) {
        this.currentDeep = currentDeep;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

}
