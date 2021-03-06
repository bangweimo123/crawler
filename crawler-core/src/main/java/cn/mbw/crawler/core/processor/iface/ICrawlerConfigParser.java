package cn.mbw.crawler.core.processor.iface;

import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;

/**
 * 针对domainTag生成一个配置项
 *
 * 针对一个crawler配置的parser
 *
 * @author mobangwei
 *
 */
public interface ICrawlerConfigParser {
    /**
     * 返回一个crawlerConfig
     *
     * @return
     */
    public CrawlerConfig parser(String domainTag);
}
