package com.dianping.merchant.robot.crawler.common.processor.plugins.downloader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.MapUtils;

import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerDownloaderTag;
import com.dianping.merchant.robot.crawler.common.processor.spider.trigger.SpiderTriggerFactory;

public class CrawlerDownloaderFactory {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(SpiderTriggerFactory.class);

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
            Map<String, Object> result = SpringLocator.getApplicationContext().getBeansOfType(Downloader.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    Object downloader = result.get(beanName);
                    CrawlerDownloaderTag downloaderTag = downloader.getClass().getAnnotation(CrawlerDownloaderTag.class);
                    if (null != downloader) {
                        String name = downloaderTag.name();
                        cache.put(name, (Downloader) downloader);
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
