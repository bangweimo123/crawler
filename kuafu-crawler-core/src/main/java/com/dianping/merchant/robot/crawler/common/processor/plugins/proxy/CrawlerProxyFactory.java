package com.dianping.merchant.robot.crawler.common.processor.plugins.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerProxyBuilder;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrlManagerFactory;

public class CrawlerProxyFactory {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(CrawlerUrlManagerFactory.class);

    private Map<String, ICrawlerProxyBuilder> cache = new HashMap<String, ICrawlerProxyBuilder>();

    public Map<String, ICrawlerProxyBuilder> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerProxyBuilder> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerProxyBuilder.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    cache.put(beanName, (ICrawlerProxyBuilder) result.get(beanName));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerProxyFactory factory = new CrawlerProxyFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerProxyFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerProxyBuilder> getCache() {
            return getInstance().getCache();
        }
    }

    public static ICrawlerProxyBuilder getProxy(String proxyName) {
        Map<String, ICrawlerProxyBuilder> cache = SingleHolder.getCache();
        if (StringUtils.isEmpty(proxyName) || !cache.containsKey(proxyName)) {
            return cache.values().iterator().next();
        }
        return cache.get(proxyName);
    }
}
