package com.dianping.merchant.robot.crawler.common.processor.plugins.scheduler;

import org.apache.commons.codec.digest.DigestUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.squirrel.client.StoreClientFactory;
import com.dianping.squirrel.client.StoreKey;
import com.dianping.squirrel.client.impl.redis.RedisStoreClient;

public class DpRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(DpRedisScheduler.class);

    private static final String CACHE_BASE_CATEGORY = "Merchant-Crawler-Robot-Scheduler";


    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    private static RedisStoreClient storeClient = (RedisStoreClient) StoreClientFactory.getStoreClientByCategory(CACHE_BASE_CATEGORY);


    public static StoreKey getSetKey(Task task) {
        return new StoreKey(CACHE_BASE_CATEGORY, SET_PREFIX, task.getUUID());
    }

    public static StoreKey getQueueKey(Task task) {
        return new StoreKey(CACHE_BASE_CATEGORY, QUEUE_PREFIX, task.getUUID());
    }

    public static StoreKey getItemKey(Task task) {
        return new StoreKey(CACHE_BASE_CATEGORY, ITEM_PREFIX, task.getUUID());

    }

    public DpRedisScheduler() {
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        storeClient.delete(getSetKey(task));
        storeClient.delete(getItemKey(task));
        storeClient.delete(getQueueKey(task));
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        try {
            boolean isDuplicate = storeClient.sismember(getSetKey(task), request.getUrl());
            if (!isDuplicate) {
                storeClient.sadd(getSetKey(task), request.getUrl());
            }
            return isDuplicate;
        } catch (Exception e) {
            LOGGER.info("isDuplicate error for request:" + request, e);
        }
        return false;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        storeClient.rpush(getQueueKey(task), request.getUrl());
        if (request.getExtras() != null) {
            @SuppressWarnings("deprecation")
            String field = DigestUtils.shaHex(request.getUrl());
            storeClient.hset(getItemKey(task), field, request);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        try {
            String url = storeClient.lpop(getQueueKey(task));
            if (url == null) {
                return null;
            }
            @SuppressWarnings("deprecation")
            String field = DigestUtils.shaHex(url);
            Request request = storeClient.hget(getItemKey(task), field);
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
        Long size = storeClient.llen(getQueueKey(task));
        return size.intValue();

    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Long size = storeClient.scard(getQueueKey(task));
        return size.intValue();
    }
}
