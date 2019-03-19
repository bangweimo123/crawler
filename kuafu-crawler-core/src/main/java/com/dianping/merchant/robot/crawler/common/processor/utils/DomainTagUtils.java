package com.dianping.merchant.robot.crawler.common.processor.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.dianping.merchant.robot.crawler.common.processor.jsonparser.CrawlerJsonParserFactory;
import com.dianping.merchant.robot.crawler.common.processor.jsonparser.ICrawlerJsonParser;
import com.google.common.collect.Lists;

public class DomainTagUtils {
    private static Map<String, String> domainTagCache = new ConcurrentHashMap<String, String>();

    public static synchronized void init() {
        List<ICrawlerJsonParser> parsers = CrawlerJsonParserFactory.getAllParsers();
        if (CollectionUtils.isNotEmpty(parsers)) {
            for (ICrawlerJsonParser parser : parsers) {
                parser.init();
            }
        }
    }

    public static void register(String domainTag, String config) {
        domainTagCache.put(domainTag, config);
    }

    public static String getForDomainTag(String domainTag) {
        if (MapUtils.isEmpty(domainTagCache)) {
            init();
        }
        return domainTagCache.get(domainTag);
    }

    public static List<String> getAllDomainTags() {
        if (MapUtils.isEmpty(domainTagCache)) {
            init();
        }
        return Lists.newArrayList(domainTagCache.keySet());
    }
}
