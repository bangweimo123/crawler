package com.lifesense.kuafu.crawler.core.processor.plugins.filters;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.spring.SpringLocator;
import com.lifesense.kuafu.crawler.core.processor.utils.DomainTagUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 过滤器工厂,将所有的过滤器都加载到内存中
 *
 * @author mobangwei
 */
public class CrawlerFilterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerFilterFactory.class);

    /**
     * key为target_type,如果不存在，则是null_type
     */
    private static Map<String, List<ICrawlerFilter>> targetFilterCacheMap = new HashMap<String, List<ICrawlerFilter>>();

    public Map<String, List<ICrawlerFilter>> getTargetFilterCacheMap() {
        return targetFilterCacheMap;
    }

    public void init() {

        Map<Integer, List<Pair<Integer, ICrawlerFilter>>> commonFilterCacheMap = new HashMap<Integer, List<Pair<Integer, ICrawlerFilter>>>();

        /**
         * 指定文件下的过滤器
         */
        Map<String, Map<Integer, List<Pair<Integer, ICrawlerFilter>>>> defFilterCacheMap = new HashMap<String, Map<Integer, List<Pair<Integer, ICrawlerFilter>>>>();
        Set<String> targetCache = new HashSet<String>();
        Set<Integer> typeCache = new HashSet<Integer>();
        try {
            @SuppressWarnings("unchecked")
            Map<String, ICrawlerFilter> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerFilter.class);
            for (String beanName : result.keySet()) {
                ICrawlerFilter filter = result.get(beanName);
                CrawlerFilterTag crawler = filter.getClass().getAnnotation(CrawlerFilterTag.class);
                String[] targets = crawler.target();
                Integer priority = crawler.priority();
                Integer type = crawler.type();
                typeCache.add(type);
                if (targets.length == 0) {
                    addCommonFilter(commonFilterCacheMap, filter, priority, type);
                } else {
                    for (String target : targets) {
                        targetCache.add(target);
                        addDefFilter(defFilterCacheMap, filter, priority, type, target);
                    }
                }
            }

            // 初始化targetFilterCacheMap
            for (String domainTag : DomainTagUtils.getAllDomainTags()) {
                for (Integer type : typeCache) {
                    String key = genCacheKey(domainTag, type);
                    targetFilterCacheMap.put(key, getTargetFilters(commonFilterCacheMap, defFilterCacheMap, domainTag, type));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String genCacheKey(String target, Integer type) {
        return target + "_" + type;
    }

    private void addCommonFilter(Map<Integer, List<Pair<Integer, ICrawlerFilter>>> cacheMap, ICrawlerFilter filter, Integer priority, Integer type) {
        List<Pair<Integer, ICrawlerFilter>> filters = cacheMap.get(type);
        if (CollectionUtils.isEmpty(filters)) {
            filters = new ArrayList<Pair<Integer, ICrawlerFilter>>();
        }
        filters.add(new ImmutablePair<Integer, ICrawlerFilter>(priority, filter));
        cacheMap.put(type, filters);
    }

    private void addDefFilter(Map<String, Map<Integer, List<Pair<Integer, ICrawlerFilter>>>> cacheMap, ICrawlerFilter filter, Integer priority, Integer type, String target) {
        Map<Integer, List<Pair<Integer, ICrawlerFilter>>> defFilterMap = cacheMap.get(target);
        if (MapUtils.isEmpty(defFilterMap)) {
            defFilterMap = new HashMap<Integer, List<Pair<Integer, ICrawlerFilter>>>();
        }
        addCommonFilter(defFilterMap, filter, priority, type);
        cacheMap.put(target, defFilterMap);
    }


    private List<ICrawlerFilter> getTargetFilters(Map<Integer, List<Pair<Integer, ICrawlerFilter>>> commonFilterCacheMap, Map<String, Map<Integer, List<Pair<Integer, ICrawlerFilter>>>> defFilterCacheMap, String target, Integer type) {
        List<Pair<Integer, ICrawlerFilter>> targetFilters = new ArrayList<Pair<Integer, ICrawlerFilter>>();
        if (defFilterCacheMap.containsKey(target)) {
            Map<Integer, List<Pair<Integer, ICrawlerFilter>>> defFilterBaseMap = defFilterCacheMap.get(target);
            if (defFilterBaseMap.containsKey(type)) {
                targetFilters.addAll(defFilterBaseMap.get(type));
            }
        }
        if (commonFilterCacheMap.containsKey(type)) {
            targetFilters.addAll(commonFilterCacheMap.get(type));
        }
        Collections.sort(targetFilters, new Comparator<Pair<Integer, ICrawlerFilter>>() {

            @Override
            public int compare(Pair<Integer, ICrawlerFilter> o1, Pair<Integer, ICrawlerFilter> o2) {
                return o1.getLeft().compareTo(o2.getLeft());
            }
        });
        List<ICrawlerFilter> resultFilters = new LinkedList<ICrawlerFilter>();
        for (Pair<Integer, ICrawlerFilter> filterItem : targetFilters) {
            resultFilters.add(filterItem.getRight());
        }
        return resultFilters;
    }

    private static class SingleHolder {
        private static CrawlerFilterFactory factory = new CrawlerFilterFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerFilterFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, List<ICrawlerFilter>> getCache() {
            return getInstance().getTargetFilterCacheMap();
        }
    }

    public static List<ICrawlerFilter> getFilters(String target, Integer type) {
        String key = genCacheKey(target, type);
        return SingleHolder.getCache().get(key);
    }

    public static List<ICrawlerFilter> getPreFilters(String target) {
        return getFilters(target, CrawlerCommonConstants.FilterConstant.PRE_FILTER_TYPE);
    }

    public static List<ICrawlerFilter> getMiddleFilters(String target) {
        return getFilters(target, CrawlerCommonConstants.FilterConstant.MID_FILTER_TYPE);
    }

    public static List<ICrawlerFilter> getAfterFilters(String target) {
        return getFilters(target, CrawlerCommonConstants.FilterConstant.AFTER_FILTER_TYPE);
    }
}
