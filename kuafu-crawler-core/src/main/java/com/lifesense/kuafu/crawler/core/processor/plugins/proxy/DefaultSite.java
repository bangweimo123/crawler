package com.lifesense.kuafu.crawler.core.processor.plugins.proxy;

import com.google.common.collect.Sets;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerProxyBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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

    public LSSite parse() {
        LSSite site = LSSite.me().setAcceptStatCode(this.getAcceptStatCode()).setCharset(this.getCharset()).setCycleRetryTimes(this.getCycleRetryTimes()).setRetryTimes(this.getRetryTimes()).setSleepTime(this.getSleepTime()).setTimeOut(this.getTimeOut()).setUseGzip(this.getUseGzip()).setUserAgent(this.getUserAgent());
        site.setUseGzip(this.getUseGzip());
        site.setHttpProxyPool("/data/lastUse.proxy");
        if (this.getProxy() > 0) {
            ICrawlerProxyBuilder crawlerProxyBuilder = CrawlerProxyFactory.getProxy(null);
            if (null != crawlerProxyBuilder) {
                int proxyNum = site.getHttpProxyPool().getProxyNum();
                if (proxyNum < this.getProxy()) {
                    List<String[]> proxyHosts = crawlerProxyBuilder.builder(10);
                    if (CollectionUtils.isNotEmpty(proxyHosts)) {
                        HttpHost httpHost = site.getHttpProxy();
                        if (null == httpHost) {
                            site.setHttpProxyPool(proxyHosts, "/data/lastUse.proxy");
                        }
                    }
                }
            }
        }
        return site;
    }
}
