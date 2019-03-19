package com.dianping.merchant.robot.crawler.biz.filter.customer;

import us.codecraft.webmagic.Page;

import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerFilterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFilter;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;

/**
 * 标签过滤
 * 
 * @author mobangwei
 * 
 */
@CrawlerFilterTag(target = {"iyiou"}, priority = 11, type = CrawlerCommonConstants.FilterConstant.AFTER_FILTER_TYPE)
public class TagCrawlerFilter implements ICrawlerFilter {

    @Override
    public ProStatus doFilter(Page page) {
        if (null != page.getResultItems().get("tag") && !page.getResultItems().get("tag").equals("餐饮")) {
            return ProStatus.fail(ProMessageCode.FITLER_ERROR.getCode());
        }
        return ProStatus.success();
    }


}
