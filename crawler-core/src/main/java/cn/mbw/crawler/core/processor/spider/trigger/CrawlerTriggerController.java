package cn.mbw.crawler.core.processor.spider.trigger;

import cn.mbw.crawler.core.processor.plugins.entity.CrawlerTrigger;
import cn.mbw.crawler.core.processor.iface.ICrawlerSpiderTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 触发器的一个管理模块
 *
 * @author mobangwei
 */
public class CrawlerTriggerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerTriggerController.class);
    private static Map<String, CrawlerTrigger> triggerCache = new ConcurrentHashMap<String, CrawlerTrigger>();
    /**
     * 触发器状态缓存,key为domainTag|trigger-type
     */
    private static Map<String, Boolean> triggerStatusCache = new ConcurrentHashMap<String, Boolean>();


    public static synchronized void registerTrigger(String domainTag, List<CrawlerTrigger> triggers) {
        for (CrawlerTrigger trigger : triggers) {
            ICrawlerSpiderTrigger spiderTrigger = SpiderTriggerFactory.getSpiderTrigger(trigger.getType());
            spiderTrigger.registerTrigger(domainTag, trigger);
            String cacheKey = getCacheKey(domainTag, trigger.getType());
            triggerCache.put(cacheKey, trigger);
            triggerStatusCache.put(cacheKey, Boolean.TRUE);
        }
    }

    public static synchronized void destoryTrigger(String domainTag, String type) {
        String cacheKey = getCacheKey(domainTag, type);
        if (triggerStatusCache.get(cacheKey)) {
            ICrawlerSpiderTrigger spiderTrigger = SpiderTriggerFactory.getSpiderTrigger(type);
            spiderTrigger.registerTrigger(domainTag, null);
            triggerStatusCache.put(cacheKey, Boolean.FALSE);
        } else {
            LOGGER.warn("current trigger is destoryed");
        }
    }

    public static synchronized void startTrigger(String domainTag, String type) {
        String cacheKey = getCacheKey(domainTag, type);
        if (!triggerStatusCache.get(cacheKey)) {
            CrawlerTrigger crawlerTrigger = triggerCache.get(cacheKey);
            ICrawlerSpiderTrigger spiderTrigger = SpiderTriggerFactory.getSpiderTrigger(crawlerTrigger.getType());
            spiderTrigger.registerTrigger(domainTag, crawlerTrigger);
            triggerStatusCache.put(cacheKey, Boolean.TRUE);
        } else {
            LOGGER.warn("current trigger is started");
        }

    }

    private static String getCacheKey(String domainTag, String triggerType) {
        return domainTag + "|" + triggerType;
    }
}
