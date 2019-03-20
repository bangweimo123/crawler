package com.lifesense.kuafu.crawler.core.processor.plugins.downloader;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerDownloaderTag;
import com.lifesense.kuafu.crawler.core.processor.spider.trigger.SpiderTriggerFactory;
import com.lifesense.kuafu.crawler.core.processor.spring.SpringLocator;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrawlerDownloaderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderTriggerFactory.class);

    private Map<String, Downloader> cache = new HashMap<String, Downloader>();

    public Map<String, Downloader> getCache() {
        return cache;
    }

    public void setCache(Map<String, Downloader> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Downloader> result = SpringLocator.getApplicationContext().getBeansOfType(Downloader.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    Downloader downloader = result.get(beanName);
                    CrawlerDownloaderTag downloaderTag = downloader.getClass().getAnnotation(CrawlerDownloaderTag.class);
                    if (null != downloader) {
                        String name = downloaderTag.name();
                        cache.put(name, downloader);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerDownloaderFactory factory = new CrawlerDownloaderFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerDownloaderFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, Downloader> getCache() {
            return getInstance().getCache();
        }
    }

    public static Downloader getDownloader(String downloaderName) {
        Downloader downloader = SingleHolder.getCache().get(downloaderName);
        if (null == downloader) {
            downloader = new HttpClientDownloader();
        }
        return downloader;
    }
}
