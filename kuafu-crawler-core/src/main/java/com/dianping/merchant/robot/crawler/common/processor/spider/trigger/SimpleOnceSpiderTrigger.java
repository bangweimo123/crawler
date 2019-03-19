package com.dianping.merchant.robot.crawler.common.processor.spider.trigger;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerTriggerTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerSpiderTrigger;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerTrigger;
import com.dianping.merchant.robot.crawler.common.processor.spider.SpiderFactory;

/**
 * 在启动的时候只执行一次
 * 
 * @author mobangwei
 * 
 */
@CrawlerTriggerTag(name = "simple_once")
public class SimpleOnceSpiderTrigger implements ICrawlerSpiderTrigger {

    @Override
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger) {
        if (null != crawlerTrigger) {
            Transaction transaction = Cat.getProducer().newTransaction("SPIDER_START", domainTag);
            try {
                Boolean runIfAfterInit = crawlerTrigger.getValue() == null ? null : (Boolean) crawlerTrigger.getValue();
                if (null != crawlerTrigger.getValue()) {
                    if (runIfAfterInit) {
                        Cat.getProducer().logEvent("SPIDER_START_EVENT", domainTag, Event.SUCCESS, "type=" + "simple_once");
                        SpiderFactory.startSpider(domainTag);
                    }
                }
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                transaction.setStatus(e);
            } finally {
                transaction.complete();
            }
        }
    }

}
