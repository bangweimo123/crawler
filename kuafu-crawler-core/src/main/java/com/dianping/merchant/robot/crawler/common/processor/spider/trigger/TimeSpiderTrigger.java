package com.dianping.merchant.robot.crawler.common.processor.spider.trigger;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerTriggerTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerSpiderTrigger;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerTrigger;

@CrawlerTriggerTag(name = "time")
public class TimeSpiderTrigger implements ICrawlerSpiderTrigger {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(TimeSpiderTrigger.class);

    @Override
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger) {
        try {
            if (null != crawlerTrigger) {
                String cronExpression = crawlerTrigger.getValue() == null ? null : (String) crawlerTrigger.getValue();
                if (StringUtils.isNotBlank(cronExpression)) {
                    Job crawlerJob = new CrawlerSpiderJob();
                    if (!QuartzManager.isExistJob(domainTag)) {
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put(CrawlerSpiderJob.CONTEXT_SPIDER_KEY, domainTag);
                        QuartzManager.addJob(domainTag, crawlerJob, cronExpression, params);
                    } else {
                        QuartzManager.modifyJobTime(domainTag, cronExpression);
                    }
                } else {
                    QuartzManager.removeJob(domainTag);
                }
            } else {
                QuartzManager.removeJob(domainTag);
            }
        } catch (Exception e) {
            LOGGER.error("time trigger error", e);
        }
    }
}
