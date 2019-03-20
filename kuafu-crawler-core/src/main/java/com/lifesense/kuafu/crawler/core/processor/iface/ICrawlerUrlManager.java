package com.lifesense.kuafu.crawler.core.processor.iface;

import com.lifesense.kuafu.crawler.core.processor.plugins.urlmanager.CrawlerUrl;

/**
 * url管理器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerUrlManager {
    /**
     * 判断url是否已经存在
     * 
     * @param url
     * @return
     */
    public boolean isExistUrl(String url);
    /**
     * 判断一个url是否已经成功处理
     * @param url
     * @return
     */
    public boolean isSuccessUrl(String url);

    /**
     * 添加一个url
     * 
     * @param url
     * @return int
     */
    public boolean addUrl(CrawlerUrl url);

    /**
     * 通过url获取相应的信息
     * 
     * @param url
     * @return
     */
    public CrawlerUrl getByUrl(String url);


    public boolean updateUrl(CrawlerUrl url);
}
