package com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerUrlManager;

public class CrawlerUrlManagerFactory {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(CrawlerUrlManagerFactory.class);

    private Map<String, ICrawlerUrlManager> cache = new HashMap<String, ICrawlerUrlManager>();

    public Map<String, ICrawlerUrlManager> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerUrlManager> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerUrlManager.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    cache.put(beanName, (ICrawlerUrlManager) result.get(beanName));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerUrlManagerFactory factory = new CrawlerUrlManagerFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerUrlManagerFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerUrlManager> getCache() {
            return getInstance().getCache();
        }
    }

    public static ICrawlerUrlManager getUrlManager(String urlManagerName) {
        Map<String, ICrawlerUrlManager> cache = SingleHolder.getCache();
        if (StringUtils.isEmpty(urlManagerName) || !cache.containsKey(urlManagerName)) {
            return cache.values().iterator().next();
        }
        return cache.get(urlManagerName);
    }
}
