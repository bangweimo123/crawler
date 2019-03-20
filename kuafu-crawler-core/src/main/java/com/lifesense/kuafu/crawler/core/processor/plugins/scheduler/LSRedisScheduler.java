package com.lifesense.kuafu.crawler.core.processor.plugins.scheduler;

import com.lifesense.base.cache.command.RedisSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

public class LSRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {
    private static final Logger LOGGER = LoggerFactory.getLogger(LSRedisScheduler.class);

    private static final String CACHE_BASE_CATEGORY = "Merchant-Crawler-Robot-Scheduler";


    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";


    public static String getSetKey(Task task) {
        return CACHE_BASE_CATEGORY + SET_PREFIX + task.getUUID();
    }

    public static String getQueueKey(Task task) {
        return CACHE_BASE_CATEGORY + QUEUE_PREFIX + task.getUUID();
    }

    public static String getItemKey(Task task) {
        return CACHE_BASE_CATEGORY + ITEM_PREFIX + task.getUUID();

    }

    public LSRedisScheduler() {
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        RedisSet setRedisSet = new RedisSet(getSetKey(task));
        setRedisSet.remove();
        RedisSet queueRedisSet = new RedisSet(getQueueKey(task));
        queueRedisSet.remove();
        RedisSet itemRedisSet = new RedisSet(getItemKey(task));
        itemRedisSet.remove();
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        try {
            RedisSet redisSet = new RedisSet(getSetKey(task));
            boolean isDuplicate = redisSet.contains(request.getUrl());
            if (!isDuplicate) {
                redisSet.add(request.getUrl());
            }
            return isDuplicate;
        } catch (Exception e) {
            LOGGER.info("isDuplicate error for request:" + request, e);
        }
        return false;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task));
        redisSet.rpush(request.getUrl());
        if (request.getExtras() != null) {
            @SuppressWarnings("deprecation")
            String field = DigestUtils.shaHex(request.getUrl());
            PluginRedisSet itemRedisSet = new PluginRedisSet(getItemKey(task));
            itemRedisSet.hset(field, request);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        try {
            PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task));
            String url = redisSet.lpop();
            if (url == null) {
                return null;
            }
            @SuppressWarnings("deprecation")
            String field = DigestUtils.shaHex(url);
            PluginRedisSet itemRedisSet = new PluginRedisSet(getItemKey(task));
            Request request = itemRedisSet.hget(field);
            if (null == request) {
                request = new Request(url);
            }
            return request;
        } catch (Exception e) {
            LOGGER.info("errror to poll task");
        }
        return null;
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task));
        Long size = redisSet.llen();
        return size.intValue();

    }

    @Override
    public int getTotalRequestsCount(Task task) {
        PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task));
        Long size = redisSet.scard();
        return size.intValue();
    }
}
