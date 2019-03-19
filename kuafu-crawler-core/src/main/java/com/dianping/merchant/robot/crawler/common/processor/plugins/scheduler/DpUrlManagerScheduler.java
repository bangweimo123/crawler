package com.dianping.merchant.robot.crawler.common.processor.plugins.scheduler;

import java.util.HashMap;
import java.util.Map;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerUrlManager;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerConfig;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrl;

/**
 * 结合redis和数据库的控制
 * 
 * @author mobangwei
 * 
 */
public class DpUrlManagerScheduler extends DpRedisScheduler {
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

    public DpUrlManagerScheduler(ICrawlerUrlManager crawlerUrlManager, CrawlerConfig crawlerConfig) {
        super();
        this.crawlerConfig = crawlerConfig;
        this.resetDuplicateCheck(toTask(crawlerConfig.getDomainTag()));
        this.crawlerUrlManager = crawlerUrlManager;
    }

    private Task toTask(final String domainTag) {
        return new Task() {

            @Override
            public String getUUID() {
                // TODO Auto-generated method stub
                return domainTag;
            }

            @Override
            public Site getSite() {
                // TODO Auto-generated method stub
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
