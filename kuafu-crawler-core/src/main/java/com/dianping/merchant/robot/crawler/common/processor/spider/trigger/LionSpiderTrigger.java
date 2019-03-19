package com.dianping.merchant.robot.crawler.common.processor.spider.trigger;

import org.apache.commons.lang3.StringUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.combiz.spring.util.LionConfigUtils;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerTriggerTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerSpiderTrigger;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerTrigger;
import com.dianping.merchant.robot.crawler.common.processor.spider.SpiderFactory;

@CrawlerTriggerTag(name = "lion")
public class LionSpiderTrigger implements ICrawlerSpiderTrigger {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(LionSpiderTrigger.class);

    public static class Constants {
        public static final String ON = "on";
        public static final String OFF = "off";
        public static final String START = "start";
        public static final String STOP = "stop";
        public static final String UN_USED = "unused";
        public static final String USED = "used";
    }

    @Override
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger) {
        if (null != crawlerTrigger) {
            String lionKey = crawlerTrigger.getValue() == null ? null : (String) crawlerTrigger.getValue();
            if (StringUtils.isNotBlank(lionKey)) {
                LionConfigUtils.getProperty(lionKey);
                addLionChangeListener(domainTag, lionKey);
            }
        }
    }

    private void addLionChangeListener(final String domainTag, final String lionKey) {
        try {
            ConfigCache.getInstance().addChange(new ConfigChange() {
                @Override
                public void onChange(String key, String value) {
                    if (lionKey.equals(key)) {
                        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_START", domainTag);
                        try {
                            if (value.equalsIgnoreCase(Constants.ON)) {
                                Cat.getProducer().logEvent("SPIDER_START_EVENT", domainTag, Event.SUCCESS, "type=" + "lion");
                                SpiderFactory.startSpider(domainTag);
                            }
                            if (value.equalsIgnoreCase(Constants.OFF)) {
                                Cat.getProducer().logEvent("SPIDER_STOP_EVENT", domainTag, Event.SUCCESS, "type=" + "lion");
                                SpiderFactory.stopSpider(domainTag);
                            }
                            if (value.equalsIgnoreCase(Constants.START)) {
                                Cat.getProducer().logEvent("SPIDER_INIT_START_EVENT", domainTag, Event.SUCCESS, "type=" + "lion");
                                SpiderFactory.initSpider(domainTag);
                                SpiderFactory.startSpider(domainTag);
                            }
                            if (value.equalsIgnoreCase(Constants.STOP)) {
                                Cat.getProducer().logEvent("SPIDER_DESTORY_EVENT", domainTag, Event.SUCCESS, "type=" + "lion");
                                SpiderFactory.destorySpider(domainTag);
                            }
                            if (value.equalsIgnoreCase(Constants.UN_USED)) {
                                Cat.getProducer().logEvent("SPIDER_TRIGGER_UNUSED", domainTag, Event.SUCCESS, "type=" + "lion");
                                CrawlerTriggerController.destoryTrigger(domainTag, "time");
                            }
                            if (value.equalsIgnoreCase(Constants.USED)) {
                                Cat.getProducer().logEvent("SPIDER_TRIGGER_USED", domainTag, Event.SUCCESS, "type=" + "lion");
                                CrawlerTriggerController.startTrigger(domainTag, "time");
                            }
                            transaction.setStatus(Transaction.SUCCESS);
                        } catch (Exception e) {
                            transaction.setStatus(e);
                        } finally {
                            transaction.complete();
                        }
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}
