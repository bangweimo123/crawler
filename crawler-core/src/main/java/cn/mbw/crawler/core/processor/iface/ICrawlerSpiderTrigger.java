package cn.mbw.crawler.core.processor.iface;

import cn.mbw.crawler.core.processor.plugins.entity.CrawlerTrigger;

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
