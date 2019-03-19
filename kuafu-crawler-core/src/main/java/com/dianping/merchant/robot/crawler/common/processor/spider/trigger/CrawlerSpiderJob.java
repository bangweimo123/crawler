package com.dianping.merchant.robot.crawler.common.processor.spider.trigger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.merchant.robot.crawler.common.processor.spider.SpiderFactory;

public class CrawlerSpiderJob implements Job {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(CrawlerSpiderJob.class);
    public static final String CONTEXT_SPIDER_KEY = "SPIDER";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String domainTag = (String) context.getJobDetail().getJobDataMap().get(CONTEXT_SPIDER_KEY);
        if (null == domainTag) {
            LOGGER.error("execute spider job error for spider is null");
        }
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_START", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_INIT_START_EVENT", domainTag, Event.SUCCESS, "type=" + "time");
            SpiderFactory.destorySpider(domainTag);
            SpiderFactory.initSpider(domainTag);
            SpiderFactory.startSpider(domainTag);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }
}
