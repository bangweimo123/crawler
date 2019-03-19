package com.dianping.merchant.robot.crawler.common.processor.iface.impl;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConfigParser;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerConfig;
import com.dianping.merchant.robot.crawler.common.processor.utils.DomainTagUtils;

/**
 * 通过json去配置
 * 
 * @author mobangwei
 * 
 */
public class JsonCrawlerConfigParser implements ICrawlerConfigParser {
    @Override
    public CrawlerConfig parser(String domainTag) {
        String jsonStr = DomainTagUtils.getForDomainTag(domainTag);
        if (StringUtils.isNotBlank(jsonStr)) {
            CrawlerConfig config = JSON.parseObject(jsonStr, CrawlerConfig.class);
            // 设置domainTag
            if (StringUtils.isBlank(config.getDomainTag())) {
                config.setDomainTag(domainTag);
            }
            return config;
        }
        return null;
    }
}
