package com.dianping.merchant.robot.crawler.common.processor.iface;

import us.codecraft.webmagic.Page;

import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.IUrlBuilder;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;

/**
 * url处理器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerUrlHandler {
    public ProStatus handler(Page page, IUrlBuilder urlBuilder);
}
