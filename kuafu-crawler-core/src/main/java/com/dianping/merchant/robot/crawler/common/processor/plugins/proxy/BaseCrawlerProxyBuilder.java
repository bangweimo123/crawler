package com.dianping.merchant.robot.crawler.common.processor.plugins.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants.ProxyBaseConstants;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerProxyBuilder;
import com.dianping.poi.crawler.export.api.HttpProxyExportService;

@Component
public class BaseCrawlerProxyBuilder implements ICrawlerProxyBuilder {

    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(BaseCrawlerProxyBuilder.class);
    @Resource
    private HttpProxyExportService httpProxyExportService;


    @Override
    public List<String[]> builder(Integer count) {
        List<String[]> resultProxyHosts = new ArrayList<String[]>();
        try {
            List<String> proxyHosts = httpProxyExportService.queryHttpProxyInfo(ProxyBaseConstants.PERIOD, ProxyBaseConstants.SCORE, count);
            if (CollectionUtils.isNotEmpty(proxyHosts)) {
                for (String proxyHost : proxyHosts) {
                    String[] proxyHostPortStr = StringUtils.split(proxyHost, ":");
                    resultProxyHosts.add(proxyHostPortStr);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("error to builder proxy ,count:" + count, e);
        }
        return resultProxyHosts;
    }
}
