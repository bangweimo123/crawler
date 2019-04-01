package com.lifesense.kuafu.crawler.core.processor.iface;

import us.codecraft.webmagic.Page;

/**
 * 转换器
 *
 * @author mobangwei
 */
public interface ICrawlerConverter {
    public Object converter(Page page, Object sourceData, Object params);
}
