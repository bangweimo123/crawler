package cn.mbw.crawler.core.processor.plugins.scheduler;

import com.lifesense.base.cache.command.RedisSet;
import com.lifesense.base.cache.support.redis.JedisProviderFactory;

public class PluginRedisSet extends RedisSet {
    public PluginRedisSet(String key) {
        super(key);
    }

    public PluginRedisSet(String key, long expireTime) {
        super(key, expireTime);
    }

    public PluginRedisSet(String key, String groupName) {
        super(key, groupName);
    }

    public PluginRedisSet(String key, String groupName, long expireTime) {
        super(key, groupName, expireTime);
    }

    public long rpush(Object obj) {
        long var1;
        try {
            byte[] data = this.valueSerialize(obj);
            if (!JedisProviderFactory.isCluster(this.groupName)) {
                var1 = JedisProviderFactory.getBinaryJedisCommands(this.groupName).rpush(this.key, data);
                return var1;
            }

            var1 = JedisProviderFactory.getBinaryJedisClusterCommands(this.groupName).rpush(this.key, data);
        } finally {
            JedisProviderFactory.getJedisProvider(this.groupName).release();
        }

        return var1;
    }

    public <T> T lpop() {
        byte[] var1;
        try {
            if (!JedisProviderFactory.isCluster(this.groupName)) {
                var1 = JedisProviderFactory.getBinaryJedisCommands(this.groupName).lpop(this.key);
            } else {
                var1 = JedisProviderFactory.getBinaryJedisClusterCommands(this.groupName).lpop(this.key);
            }

        } finally {
            JedisProviderFactory.getJedisProvider(this.groupName).release();
        }
        return this.valueDerialize(var1);
    }

    public long hset(String field, Object obj) {
        long var1;
        try {
            byte[] data = this.valueSerialize(obj);
            byte[] fieldKey = this.valueSerialize(field);
            if (!JedisProviderFactory.isCluster(this.groupName)) {
                var1 = JedisProviderFactory.getBinaryJedisCommands(this.groupName).hset(this.key, fieldKey, data);
                return var1;
            }else {
                var1 = JedisProviderFactory.getBinaryJedisClusterCommands(this.groupName).hset(this.key, fieldKey, data);
            }
        } finally {
            JedisProviderFactory.getJedisProvider(this.groupName).release();
        }
        return var1;
    }

    public <T> T hget(String field) {
        byte[] var1;
        try {
            byte[] fieldKey = this.valueSerialize(field);
            if (!JedisProviderFactory.isCluster(this.groupName)) {
                var1 = JedisProviderFactory.getBinaryJedisCommands(this.groupName).hget(this.key, fieldKey);
            } else {
                var1 = JedisProviderFactory.getBinaryJedisClusterCommands(this.groupName).hget(this.key, fieldKey);
            }
        } finally {
            JedisProviderFactory.getJedisProvider(this.groupName).release();
        }
        return this.valueDerialize(var1);
    }

    public long llen() {
        long var1;
        try {
            if (!JedisProviderFactory.isCluster(this.groupName)) {
                var1 = JedisProviderFactory.getBinaryJedisCommands(this.groupName).llen(this.key);
            } else {
                var1 = JedisProviderFactory.getBinaryJedisClusterCommands(this.groupName).llen(this.key);
            }

        } finally {
            JedisProviderFactory.getJedisProvider(this.groupName).release();
        }
        return var1;
    }

    public long scard() {
        long var1;
        try {
            if (!JedisProviderFactory.isCluster(this.groupName)) {
                var1 = JedisProviderFactory.getBinaryJedisCommands(this.groupName).scard(this.key);
            } else {
                var1 = JedisProviderFactory.getBinaryJedisClusterCommands(this.groupName).scard(this.key);
            }

        } finally {
            JedisProviderFactory.getJedisProvider(this.groupName).release();
        }
        return var1;
    }

}
