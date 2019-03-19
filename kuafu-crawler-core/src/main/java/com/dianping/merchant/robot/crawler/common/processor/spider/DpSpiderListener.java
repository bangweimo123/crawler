package com.dianping.merchant.robot.crawler.common.processor.spider;

import java.util.HashMap;
import java.util.Map;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.scheduler.Scheduler;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerUrlManager;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrl;

/**
 * 针对起始错误进行容错处理
 * 
 * @author mobangwei
 * 
 */
public class DpSpiderListener implements SpiderListener {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(DpSpiderListener.class);
    private Scheduler scheduler;

    private DpSpider spider;

    private ICrawlerUrlManager crawlerUrlManager;

    public DpSpiderListener(Scheduler scheduler, DpSpider spider, ICrawlerUrlManager crawlerUrlManager) {
        super();
        this.scheduler = scheduler;
        this.spider = spider;
        this.crawlerUrlManager = crawlerUrlManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public DpSpider getSpider() {
        return spider;
    }

    public void setSpider(DpSpider spider) {
        this.spider = spider;
    }

    @Override
    public void onSuccess(Request request) {
        // TODO Auto-generated method stub

    }

    // 处理失败后，需要将请求重新塞回请求链路中
    @Override
    public void onError(Request request) {
        Object proStatus = request.getExtra("proStatus");
        if (null != proStatus) {
            ProStatus status = (ProStatus) proStatus;
            CrawlerUrl crawlerUrl = new CrawlerUrl();
            crawlerUrl.setUrl(request.getUrl());
            // 如果是下载失败,不做任何处理
            if (status.getMessageCode() == ProMessageCode.DOWNLOAD_ERROR.getCode()) {
                LOGGER.warn("download error url:" + request.getUrl());
                crawlerUrl.setMessageCode(status.getMessageCode());
                crawlerUrl.setStatus(status.isSuccess() ? 1 : 2);
            }
            if (status.getMessageCode() == ProMessageCode.LOCK.getCode()) {
                crawlerUrl.setMessageCode(ProMessageCode.DEFAULT.getCode());
                crawlerUrl.setStatus(0);
            }
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("message", status.getMessage());
            extras.put("domainTag", spider.getDomainTag());
            crawlerUrlManager.updateUrl(crawlerUrl);
            return;
        }
        scheduler.push(request, spider);
    }
}
