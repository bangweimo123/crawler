package com.lifesense.kuafu.crawler.core.test;

import com.lifesense.kuafu.crawler.core.base.BaseTest;
import cn.mbw.crawler.core.processor.plugins.proxy.LSProxyPool;
import org.apache.http.HttpHost;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class KuafuCrawlerProxyTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KuafuCrawlerProxyTest.class);

    @Test
    public void testProxyFile() {
        List<String[]> result = new ArrayList();
        result.add(new String[]{"49.86.182.212", "9999"});
        LSProxyPool proxyPool = new LSProxyPool(result, "/data/lastUse.proxy");
        HttpHost httpProxy = proxyPool.getProxy();
        System.out.println(123);
    }
}
