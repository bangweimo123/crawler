package cn.mbw.crawler.core.processor.spider.trigger;

import cn.mbw.crawler.core.processor.spider.SpiderFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerSpiderJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerSpiderJob.class);
    public static final String CONTEXT_SPIDER_KEY = "SPIDER";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String domainTag = (String) context.getJobDetail().getJobDataMap().get(CONTEXT_SPIDER_KEY);
        if (null == domainTag) {
            LOGGER.error("execute spider job error for spider is null");
        }
        SpiderFactory.destroySpider(domainTag);
        SpiderFactory.initSpider(domainTag);
        SpiderFactory.startSpider(domainTag);
    }
}
