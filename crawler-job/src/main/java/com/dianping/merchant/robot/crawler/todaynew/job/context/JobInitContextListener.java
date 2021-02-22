package com.dianping.merchant.robot.crawler.todaynew.job.context;

import com.lifesense.kuafu.crawler.core.processor.spider.SpiderFactory;
import com.lifesense.kuafu.crawler.core.processor.utils.DomainTagUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.util.List;

/**
 * job启动初始化
 *
 * @author mobangwei
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
                SpiderFactory.destroySpider(domainTag);
            }
        }
        super.contextDestroyed(event);
    }


}
