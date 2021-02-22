package cn.mbw.crawler.core.processor.plugins.scheduler;

import java.util.HashMap;
import java.util.Map;

import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;
import cn.mbw.crawler.core.processor.plugins.urlmanager.CrawlerUrl;
import com.google.common.collect.Maps;
import cn.mbw.crawler.core.processor.iface.ICrawlerUrlManager;
import org.apache.commons.collections.MapUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

import cn.mbw.crawler.core.processor.plugins.entity.ProMessageCode;

/**
 * 结合redis和数据库的控制
 *
 * @author mobangwei
 */
public class UrlManagerScheduler extends LSRedisScheduler {
    private ICrawlerUrlManager crawlerUrlManager;

    private CrawlerConfig crawlerConfig;


    public CrawlerConfig getCrawlerConfig() {
        return crawlerConfig;
    }

    public void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public ICrawlerUrlManager getCrawlerUrlManager() {
        return crawlerUrlManager;
    }

    public void setCrawlerUrlManager(ICrawlerUrlManager crawlerUrlManager) {
        this.crawlerUrlManager = crawlerUrlManager;
    }

    public UrlManagerScheduler(ICrawlerUrlManager crawlerUrlManager, CrawlerConfig crawlerConfig) {
        super();
        this.crawlerConfig = crawlerConfig;
        this.resetDuplicateCheck(toTask(crawlerConfig.getDomainTag()));
        this.crawlerUrlManager = crawlerUrlManager;
    }

    private Task toTask(final String domainTag) {
        return new Task() {

            @Override
            public String getUUID() {
                return domainTag;
            }

            @Override
            public Site getSite() {
                return null;
            }
        };
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        if (!crawlerUrlManager.isExistUrl(request.getUrl())) {
            CrawlerUrl crawlerUrl = new CrawlerUrl();
            crawlerUrl.setUrl(request.getUrl());
            crawlerUrl.setStatus(0);
            crawlerUrl.setMessageCode(ProMessageCode.LOCK.getCode());
            crawlerUrl.setBaseUrl(crawlerConfig.getCrawlerBaseInfo().getBaseUrl());
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("domainTag", crawlerConfig.getDomainTag());
            crawlerUrl.setExtra(extras);
            crawlerUrlManager.addUrl(crawlerUrl);
        } else {
            CrawlerUrl crawlerUrl = crawlerUrlManager.getByUrl(request.getUrl());
            if (!crawlerUrl.getMessageCode().equals(ProMessageCode.SUCCESS.getCode())) {
                crawlerUrl.setStatus(0);
                crawlerUrl.setMessageCode(ProMessageCode.LOCK.getCode());
                Map<String, Object> extras = crawlerUrl.getExtra();
                if (MapUtils.isEmpty(extras)) {
                    extras = Maps.newHashMap();
                }
                extras.put("domainTag", crawlerConfig.getDomainTag());
                crawlerUrl.setExtra(extras);
                crawlerUrlManager.updateUrl(crawlerUrl);
            } else {
                // 如果这个url已经成功入库，表示已经不需要重新拉取
                return;
            }
        }
        super.pushWhenNoDuplicate(request, task);
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        if (crawlerUrlManager.isSuccessUrl(request.getUrl())) {
            return true;
        }
        return super.isDuplicate(request, task);
    }
}
