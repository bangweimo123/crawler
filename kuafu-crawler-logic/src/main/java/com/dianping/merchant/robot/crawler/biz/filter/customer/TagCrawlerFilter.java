package com.dianping.merchant.robot.crawler.biz.filter.customer;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProMessageCode;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import us.codecraft.webmagic.Page;

/**
 * 标签过滤
 *
 * @author mobangwei
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
