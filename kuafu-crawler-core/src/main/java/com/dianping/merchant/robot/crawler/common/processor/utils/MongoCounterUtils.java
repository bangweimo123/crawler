package com.dianping.merchant.robot.crawler.common.processor.utils;

import com.dianping.avatar.counter.mongo.MongoCounter;
import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mongo计数器，针对某个domainTag进行一次爬取的计数
 * 
 * @author mobangwei
 * 
 */
public class MongoCounterUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCounterUtils.class);
    private static final String CATAGRAY_KEY = "url-crawler-count";
    private static final Long INCR_BY = 1l;

    private static MongoCounter getCounter() {
        MongoCounter mongoCounter = SpringLocator.getBean(MongoCounter.class);
        return mongoCounter;
    }

    public static Integer incrAndReturn(String domainTag) {
        Long currentCount = getCounter().incrAndReturn(CATAGRAY_KEY, domainTag, null, INCR_BY);
        if (null == currentCount) {
            return 0;
        } else {
            return currentCount.intValue();
        }
    }

    public static void reset(String domainTag) {
        boolean isReset = getCounter().reset(CATAGRAY_KEY, domainTag, null);
        if (!isReset) {
            LOGGER.error("reset domaintag counter error ,domainTag:" + domainTag);
        }
    }
}
