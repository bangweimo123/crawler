package com.lifesense.kuafu.crawler.core.processor.spider.trigger;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerTriggerTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerSpiderTrigger;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.CrawlerTrigger;
import com.lifesense.kuafu.crawler.core.processor.spider.SpiderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在启动的时候只执行一次
 *
 * @author mobangwei
 */
@CrawlerTriggerTag(name = "simple_once")
public class SimpleOnceSpiderTrigger implements ICrawlerSpiderTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleOnceSpiderTrigger.class);

    @Override
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger) {
        if (null != crawlerTrigger) {
            try {
                Boolean runIfAfterInit = crawlerTrigger.getValue() == null ? null : (Boolean) crawlerTrigger.getValue();
                if (null != crawlerTrigger.getValue()) {
                    if (runIfAfterInit) {
                        SpiderFactory.startSpider(domainTag);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("exception for registerTrigger", e);
            }
        }
    }

}
