package cn.mbw.crawler.core.processor.spider.trigger;

import cn.mbw.crawler.core.processor.annotation.CrawlerTriggerTag;
import cn.mbw.crawler.core.processor.plugins.entity.CrawlerTrigger;
import cn.mbw.crawler.core.processor.iface.ICrawlerSpiderTrigger;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@CrawlerTriggerTag(name = "time")
public class TimeSpiderTrigger implements ICrawlerSpiderTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSpiderTrigger.class);

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
