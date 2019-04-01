package com.lifesense.kuafu.crawler.core.processor.plugins.scheduler;

import com.lifesense.base.cache.command.RedisObject;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerUrlManager;
import com.lifesense.kuafu.crawler.core.processor.plugins.urlmanager.CrawlerUrl;
import org.springframework.stereotype.Component;

@Component
public class RedisUrlManager implements ICrawlerUrlManager {
    private static final String KUAFU_CRAWLER_GROUP = "kuafu";
    private static final String REDIS_GROUP = "kuafu_crawler_url_manager_u14";

    @Override
    public boolean isExistUrl(String url) {
        RedisObject item = new RedisObject(REDIS_GROUP + url, KUAFU_CRAWLER_GROUP);
        item.remove();
        return item.exists();
    }

    @Override
    public boolean isSuccessUrl(String url) {
        RedisObject item = new RedisObject(REDIS_GROUP + url, KUAFU_CRAWLER_GROUP);
        //       CrawlerUrl result = item.get();
//        if (null == result) {
//            return false;
//        } else {
//            return result.getStatus() == 1;
//        }
        return false;
    }

    @Override
    public boolean addUrl(CrawlerUrl url) {
        RedisObject item = new RedisObject(REDIS_GROUP + url.getUrl(), KUAFU_CRAWLER_GROUP);
        return item.set(url);
    }

    @Override
    public CrawlerUrl getByUrl(String url) {
        RedisObject item = new RedisObject(REDIS_GROUP + url, KUAFU_CRAWLER_GROUP);
        return item.get();
    }

    @Override
    public boolean updateUrl(CrawlerUrl url) {
        RedisObject item = new RedisObject(REDIS_GROUP + url.getUrl(), KUAFU_CRAWLER_GROUP);
        return item.set(url);
    }
}
