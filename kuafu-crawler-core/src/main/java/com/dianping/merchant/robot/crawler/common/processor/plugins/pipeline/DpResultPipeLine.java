package com.dianping.merchant.robot.crawler.common.processor.plugins.pipeline;

import java.util.HashMap;
import java.util.Map;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerResultHandler;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerUrlManager;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProcessorContext;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrl;

/**
 * 将结果集合过滤后导出到数据库中
 * 
 * @author mobangwei
 * 
 */
public class DpResultPipeLine implements Pipeline {


    private ICrawlerResultHandler crawlerResultHandler;

    private ICrawlerUrlManager crawlerUrlManager;

    public ICrawlerResultHandler getCrawlerResultHandler() {
        return crawlerResultHandler;
    }

    public void setCrawlerResultHandler(ICrawlerResultHandler crawlerResultHandler) {
        this.crawlerResultHandler = crawlerResultHandler;
    }


    public ICrawlerUrlManager getCrawlerUrlManager() {
        return crawlerUrlManager;
    }

    public void setCrawlerUrlManager(ICrawlerUrlManager crawlerUrlManager) {
        this.crawlerUrlManager = crawlerUrlManager;
    }

    public DpResultPipeLine(ICrawlerUrlManager crawlerUrlManager, ICrawlerResultHandler crawlerResultHandler) {
        this.crawlerUrlManager = crawlerUrlManager;
        this.crawlerResultHandler = crawlerResultHandler;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        ProcessorContext context = ProcessorContext.getContext(resultItems);
        // 一个页面爬取的状态
        ProStatus status = context.getProStatus();
        if (status.isSuccess()) {
            status = crawlerResultHandler.handler(resultItems.getAll(), context.getParams());
        }
        CrawlerUrl crawlerUrl = new CrawlerUrl();
        crawlerUrl.setUrl((String) context.getParam("currentUrl"));
        crawlerUrl.setMessageCode(status.getMessageCode());
        crawlerUrl.setStatus(status.isSuccess() ? 1 : 2);
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put("message", status.getMessage());
        crawlerUrl.setExtra(extras);
        crawlerUrlManager.updateUrl(crawlerUrl);
    }

}
