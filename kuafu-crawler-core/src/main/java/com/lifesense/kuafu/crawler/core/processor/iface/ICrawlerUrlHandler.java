package com.lifesense.kuafu.crawler.core.processor.iface;

import us.codecraft.webmagic.Page;

import com.lifesense.kuafu.crawler.core.processor.plugins.entity.IUrlBuilder;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;

/**
 * url处理器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerUrlHandler {
    public ProStatus handler(Page page, IUrlBuilder urlBuilder);
}
