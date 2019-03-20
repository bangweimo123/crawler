package com.lifesense.kuafu.crawler.core.processor.plugins.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerProxyBuilder;
import us.codecraft.webmagic.Site;

import com.lifesense.kuafu.crawler.core.processor.plugins.proxy.CrawlerProxyFactory;
import com.google.common.collect.Sets;

public class DefaultSite implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2454212203222639726L;
    /**
     * 重试次数
     */
    private Integer retryTimes = 3;
    /**
     * 睡眠时间
     */
    private Integer sleepTime = 2000;
    /**
     * 编码
     */
    private String charset = "utf-8";
    /**
     * 超时时间
     */
    private Integer timeOut = 5000;

    private Integer cycleRetryTimes = 0;
    /**
     * 可访问的状态码
     */
    private Set<Integer> acceptStatCode = Sets.newHashSet(200);
    /**
     * 默认百度userAgent
     */
    private String userAgent = "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)";

    private Integer proxy = 0;

    private Boolean useGzip = false;

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Integer getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(Integer cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public Set<Integer> getAcceptStatCode() {
        return acceptStatCode;
    }

    public void setAcceptStatCode(Set<Integer> acceptStatCode) {
        this.acceptStatCode = acceptStatCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getProxy() {
        return proxy;
    }

    public void setProxy(Integer proxy) {
        this.proxy = proxy;
    }

    public Boolean getUseGzip() {
        return useGzip;
    }

    public void setUseGzip(Boolean useGzip) {
        this.useGzip = useGzip;
    }

    public Site parse() {
        Site site = Site.me().setAcceptStatCode(this.getAcceptStatCode()).setCharset(this.getCharset()).setCycleRetryTimes(this.getCycleRetryTimes()).setRetryTimes(this.getRetryTimes()).setSleepTime(this.getSleepTime()).setTimeOut(this.getTimeOut()).setUseGzip(this.getUseGzip()).setUserAgent(this.getUserAgent());
        site.setUseGzip(this.getUseGzip());
        if (this.getProxy() > 0) {
            ICrawlerProxyBuilder crawlerProxyBuilder = CrawlerProxyFactory.getProxy(null);
            List<String[]> proxyHosts = crawlerProxyBuilder.builder(this.getProxy());
            site.setHttpProxyPool(proxyHosts);
        }
        return site;
    }
}
