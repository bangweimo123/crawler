package cn.mbw.crawler.core.processor.plugins.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 基础的url的设置
 *
 * @author mobangwei
 *
 */
public class CrawlerBaseInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2257821138908166856L;
    /**
     * 主地址
     *
     * @return
     */
    private String baseUrl;
    /**
     * 线程数
     */
    private Integer threadCount = 3;
    /**
     * url管理实现类
     */
    private String urlManager;
    /**
     * 结果处理类
     */
    private String resultHandler;
    /**
     * 监听器
     */
    private String listener;

    /**
     * 是否使用带有js的下载器
     */
    private Map<String, String> downloaderPlugins;
    /**
     * 限制的月份
     */
    private Integer limitMonth = 1;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public String getUrlManager() {
        return urlManager;
    }

    public void setUrlManager(String urlManager) {
        this.urlManager = urlManager;
    }

    public String getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(String resultHandler) {
        this.resultHandler = resultHandler;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public Map<String, String> getDownloaderPlugins() {
        return downloaderPlugins;
    }

    public void setDownloaderPlugins(Map<String, String> downloaderPlugins) {
        this.downloaderPlugins = downloaderPlugins;
    }

    public Integer getLimitMonth() {
        return limitMonth;
    }

    public void setLimitMonth(Integer limitMonth) {
        this.limitMonth = limitMonth;
    }


}
