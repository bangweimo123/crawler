package cn.mbw.crawler.core.processor.plugins.resulthandler;

import cn.mbw.crawler.core.processor.iface.ICrawlerResultHandler;
import cn.mbw.crawler.core.processor.spring.SpringLocator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrawlerResultHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerResultHandlerFactory.class);

    private Map<String, ICrawlerResultHandler> cache = new HashMap<String, ICrawlerResultHandler>();

    public Map<String, ICrawlerResultHandler> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerResultHandler> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ICrawlerResultHandler> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerResultHandler.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    cache.put(beanName, result.get(beanName));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerResultHandlerFactory factory = new CrawlerResultHandlerFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerResultHandlerFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerResultHandler> getCache() {
            return getInstance().getCache();
        }
    }

    public static ICrawlerResultHandler getResultHandler(String resultHandlerName) {
        Map<String, ICrawlerResultHandler> cache = SingleHolder.getCache();
        if (StringUtils.isEmpty(resultHandlerName) || !cache.containsKey(resultHandlerName)) {
            return cache.values().iterator().next();
        }
        return cache.get(resultHandlerName);
    }
}
