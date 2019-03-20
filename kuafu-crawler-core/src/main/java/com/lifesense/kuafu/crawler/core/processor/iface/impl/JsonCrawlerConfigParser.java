package com.lifesense.kuafu.crawler.core.processor.iface.impl;

import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConfigParser;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.CrawlerConfig;
import com.lifesense.kuafu.crawler.core.processor.utils.DomainTagUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

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
