package com.dianping.merchant.robot.crawler.common.processor.jsonparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.MapUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrlManagerFactory;
import com.google.common.collect.Lists;

/**
 * json配置文件获取类
 * 
 * @author mobangwei
 * 
 */
public class CrawlerJsonParserFactory {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(CrawlerUrlManagerFactory.class);
    private Map<String, ICrawlerJsonParser> cache = new HashMap<String, ICrawlerJsonParser>();

    public Map<String, ICrawlerJsonParser> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerJsonParser> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerJsonParser.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    cache.put(beanName, (ICrawlerJsonParser) result.get(beanName));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerJsonParserFactory factory = new CrawlerJsonParserFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerJsonParserFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerJsonParser> getCache() {
            return getInstance().getCache();
        }
    }

    public static List<ICrawlerJsonParser> getAllParsers() {
        Map<String, ICrawlerJsonParser> cache = SingleHolder.getCache();
        if (MapUtils.isNotEmpty(cache)) {
            return Lists.newArrayList(cache.values());
        }
        return null;
    }
}
