package com.lifesense.kuafu.crawler.core.processor.plugins.filters;

import java.util.Date;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import org.apache.commons.lang3.time.DateUtils;

import us.codecraft.webmagic.Page;

import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProMessageCode;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProcessorContext;

/**
 * 日期过滤，将里面的日期过滤掉
 *
 * @author mobangwei
 */
@CrawlerFilterTag(priority = 5, type = CrawlerCommonConstants.FilterConstant.AFTER_FILTER_TYPE)
public class LimitDateCrawlerFilter implements ICrawlerFilter {
    private static final Date ONE_MONTH_DATE = DateUtils.addMonths(new Date(), -1);

    @Override
    public ProStatus doFilter(Page page) {
        Date sourceDate = page.getResultItems().get("date");
        if (null == sourceDate) {
            return ProStatus.success();
        }
        Integer limitMonth = (Integer) ProcessorContext.getContext(page).getParam(CrawlerCommonConstants.ProcessorContextConstant.LIMIT_MONTH);
        Date limitMonthDate = getDate(limitMonth);
        if (null != sourceDate && limitMonthDate.compareTo(sourceDate) <= 0) {
            return ProStatus.success();
        }
        return ProStatus.fail(ProMessageCode.FITLER_LIMITDATE_ERROR.getCode(), "date:" + sourceDate);
    }

    private Date getDate(Integer limitMonth) {
        if (null == limitMonth) {
            return ONE_MONTH_DATE;
        }
        return DateUtils.addMonths(new Date(), -limitMonth);
    }

}
