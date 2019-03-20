package com.lifesense.kuafu.crawler.core.processor.utils;

import com.lifesense.base.cache.command.RedisNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mongo计数器，针对某个domainTag进行一次爬取的计数
 *
 * @author mobangwei
 */
public class RedisCountUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCountUtils.class);
    private static final String CATAGRAY_KEY = "url-crawler-count";
    private static final Long INCR_BY = 1l;

    public static Integer incrAndReturn(String domainTag) {
        RedisNumber redisNumber = new RedisNumber(CATAGRAY_KEY + domainTag);
        Long currentCount = redisNumber.incrBy(INCR_BY);
        if (null == currentCount) {
            return 0;
        } else {
            return currentCount.intValue();
        }
    }

    public static void reset(String domainTag) {
        RedisNumber redisNumber = new RedisNumber(CATAGRAY_KEY + domainTag);
        boolean isReset = redisNumber.set(0l);
        if (!isReset) {
            LOGGER.error("reset domaintag counter error ,domainTag:" + domainTag);
        }
    }
}
