package com.dianping.merchant.robot.crawler.biz.filter.customer;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.HttpConstant;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerFilterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFilter;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;

/**
 * 重定向的过滤
 * 
 * @author mobangwei
 * 
 */
@CrawlerFilterTag(target = {"iyiou", "zhcyw"}, priority = 12, type = CrawlerCommonConstants.FilterConstant.AFTER_FILTER_TYPE)
public class RedirectCheckCrawlerFilter implements ICrawlerFilter {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(RedirectCheckCrawlerFilter.class);

    @Override
    public ProStatus doFilter(Page page) {
        String requestMethod = page.getRequest().getMethod();
        Integer code = page.getStatusCode();
        if (HttpConstant.Method.POST.equalsIgnoreCase(requestMethod) && code == 302) {
            LOGGER.info("method:" + requestMethod + ",code=" + code);
            return ProStatus.fail(ProMessageCode.FITLER_REDIRECT_ERROR.getCode());
        }
        return ProStatus.success();
    }
}
