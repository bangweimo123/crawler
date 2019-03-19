package com.dianping.merchant.robot.crawler.common.processor.jsonparser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.util.LionConfigUtils;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.merchant.robot.crawler.common.processor.spider.SpiderFactory;
import com.dianping.merchant.robot.crawler.common.processor.utils.DomainTagUtils;

/**
 * 通过lion配置去动态变更
 * 
 * @author mobangwei
 * 
 */
@Component
public class LionCrawlerJsonParser implements ICrawlerJsonParser {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(LionCrawlerJsonParser.class);

    private static final String LION_KEY_PREFIX = "merchant-today-news-frontground-service.crawler.config";

    private static final String LION_KEY_DOMAINTAGS = "merchant-today-news-frontground-service.crawler.config.domainTags";

    @Override
    public void init() {
        readLion();

    }

    private void readLion() {
        String domainTagsStr = LionConfigUtils.getProperty(LION_KEY_DOMAINTAGS);
        if (StringUtils.isNotBlank(domainTagsStr)) {
            String[] allDomainTags = StringUtils.split(domainTagsStr, ",");
            for (String domainTag : allDomainTags) {
                String realKey = StringUtils.join(LION_KEY_PREFIX, ".", domainTag);
                String realValue = LionConfigUtils.getProperty(realKey);
                DomainTagUtils.register(domainTag, realValue);
                addLionChangeListener(domainTag, realKey);
            }
        }
    }

    private void addLionChangeListener(final String domainTag, final String lionKey) {
        try {
            ConfigCache.getInstance().addChange(new ConfigChange() {
                @Override
                public void onChange(String key, String value) {
                    if (lionKey.equals(key)) {
                        // 如果lion值变化，可以动态的重新定义爬虫，达到动态变化配置的目的
                        DomainTagUtils.register(domainTag, value);
                        SpiderFactory.initSpider(domainTag);
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
