package com.dianping.merchant.robot.crawler.todaynew.job.context;

import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.dianping.merchant.robot.crawler.common.processor.spider.SpiderFactory;
import com.dianping.merchant.robot.crawler.common.processor.utils.DomainTagUtils;

/**
 * job启动初始化
 * 
 * @author mobangwei
 * 
 */
public class JobInitContextListener extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
        if (CollectionUtils.isNotEmpty(allDomainTags)) {
            for (String domainTag : allDomainTags) {
                SpiderFactory.initSpider(domainTag);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
        if (CollectionUtils.isNotEmpty(allDomainTags)) {
            for (String domainTag : allDomainTags) {
                SpiderFactory.destorySpider(domainTag);
            }
        }
        super.contextDestroyed(event);
    }


}
