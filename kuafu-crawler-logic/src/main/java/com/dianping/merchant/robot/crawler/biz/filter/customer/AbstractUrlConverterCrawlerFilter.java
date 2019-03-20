package com.dianping.merchant.robot.crawler.biz.filter.customer;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import us.codecraft.webmagic.Page;

@CrawlerFilterTag(target = {"hupogu"}, priority = 1, type = CrawlerCommonConstants.FilterConstant.PRE_FILTER_TYPE)
public class AbstractUrlConverterCrawlerFilter implements ICrawlerFilter {

    @Override
    public ProStatus doFilter(Page page) {
        String result = page.getRawText().replaceAll("\\./", "\\.\\./");
        page.setRawText(result);
        return ProStatus.success();
    }
}
