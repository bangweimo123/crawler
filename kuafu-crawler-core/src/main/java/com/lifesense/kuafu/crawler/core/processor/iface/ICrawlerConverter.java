package com.lifesense.kuafu.crawler.core.processor.iface;

/**
 * 转换器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerConverter {
    public Object converter(Object sourceData,Object params);
}
