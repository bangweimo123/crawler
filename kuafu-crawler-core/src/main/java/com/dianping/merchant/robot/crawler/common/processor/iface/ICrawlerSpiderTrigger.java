package com.dianping.merchant.robot.crawler.common.processor.iface;

import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerTrigger;

/**
 * 调度触发器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerSpiderTrigger {

    /**
     * 一般trigger为null表示关闭
     * 
     * @param domainTag
     * @param crawlerTrigger
     */
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger);
}
