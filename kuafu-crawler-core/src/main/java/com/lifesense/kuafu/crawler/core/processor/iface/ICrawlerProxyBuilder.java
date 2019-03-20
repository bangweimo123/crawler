package com.lifesense.kuafu.crawler.core.processor.iface;

import java.util.List;

/**
 * 构建代理接口
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerProxyBuilder {
    /**
     * 构建一个代理
     * 
     * @return
     */
    public List<String[]> builder(Integer count);
}
