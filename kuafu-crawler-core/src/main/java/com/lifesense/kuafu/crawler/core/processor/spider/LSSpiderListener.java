package com.lifesense.kuafu.crawler.core.processor.spider;

import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerUrlManager;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProMessageCode;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import com.lifesense.kuafu.crawler.core.processor.plugins.urlmanager.CrawlerUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * 针对起始错误进行容错处理
 *
 * @author mobangwei
 */
public class LSSpiderListener implements SpiderListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LSSpiderListener.class);
    private Scheduler scheduler;

    private LSSpider spider;

    private ICrawlerUrlManager crawlerUrlManager;

    public LSSpiderListener(Scheduler scheduler, LSSpider spider, ICrawlerUrlManager crawlerUrlManager) {
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


    public LSSpider getSpider() {
        return spider;
    }

    public void setSpider(LSSpider spider) {
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
