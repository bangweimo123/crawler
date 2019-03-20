package com.lifesense.kuafu.crawler.core.processor.iface;

import us.codecraft.webmagic.Page;

import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;

/**
 * 过滤器，对于爬取的内容进行过滤,相当于一个拦截器的功能
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerFilter {
    /**
     * 针对页面分析的一些情况进行过滤
     * 
     * @param page
     * @return
     */
    ProStatus doFilter(Page page);
}
