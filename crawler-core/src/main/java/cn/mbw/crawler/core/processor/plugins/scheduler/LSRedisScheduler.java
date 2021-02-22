package cn.mbw.crawler.core.processor.plugins.scheduler;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

public class LSRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {
    private static final Logger LOGGER = LoggerFactory.getLogger(LSRedisScheduler.class);

    private static final String CACHE_BASE_CATEGORY = "kuafu-crawler-scheduler14_";


    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    //用户缓存组名
    private static final String KUAFU_CRAWLER_GROUP = "kuafu";

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
        PluginRedisSet setRedisSet = new PluginRedisSet(getSetKey(task), KUAFU_CRAWLER_GROUP);
        if (setRedisSet.exists()) {
            setRedisSet.remove();
        }
        PluginRedisSet queueRedisSet = new PluginRedisSet(getQueueKey(task), KUAFU_CRAWLER_GROUP);
        if (queueRedisSet.exists()) {
            queueRedisSet.remove();
        }
        PluginRedisSet itemRedisSet = new PluginRedisSet(getItemKey(task), KUAFU_CRAWLER_GROUP);
        if (itemRedisSet.exists()) {
            itemRedisSet.remove();
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        try {
            PluginRedisSet redisSet = new PluginRedisSet(getSetKey(task), KUAFU_CRAWLER_GROUP);
            boolean isDuplicate = redisSet.contains(request.getUrl());
            if (!isDuplicate) {
                redisSet.add(request.getUrl());
            } else {
                LOGGER.warn("isDuplicate url for request:" + request.getUrl());
            }
            return isDuplicate;
        } catch (Exception e) {
            LOGGER.info("isDuplicate error for request:" + request, e);
        }
        //TODO
        return false;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task), KUAFU_CRAWLER_GROUP);
        long pushSize = redisSet.rpush(request.getUrl());
        if (pushSize <= 0) {
            LOGGER.warn("push queue error");
        }
        if (request.getExtras() != null) {
            @SuppressWarnings("deprecation")
            String field = DigestUtils.shaHex(request.getUrl());
            PluginRedisSet itemRedisSet = new PluginRedisSet(getItemKey(task), KUAFU_CRAWLER_GROUP);
            itemRedisSet.hset(field, JSON.toJSONString(request));
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        try {
            PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task), KUAFU_CRAWLER_GROUP);
            String url = redisSet.lpop();
            if (url == null) {
                return null;
            }
            @SuppressWarnings("deprecation")
            String field = DigestUtils.shaHex(url);
            PluginRedisSet itemRedisSet = new PluginRedisSet(getItemKey(task), KUAFU_CRAWLER_GROUP);
            Request request = null;
            String requestStr = itemRedisSet.hget(field);
            if (StringUtils.isNotBlank(requestStr)) {
                request = JSON.parseObject(requestStr, Request.class);
            }
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
        PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task), KUAFU_CRAWLER_GROUP);
        Long size = redisSet.llen();
        return size.intValue();

    }

    @Override
    public int getTotalRequestsCount(Task task) {
        PluginRedisSet redisSet = new PluginRedisSet(getQueueKey(task), KUAFU_CRAWLER_GROUP);
        Long size = redisSet.scard();
        return size.intValue();
    }
}
