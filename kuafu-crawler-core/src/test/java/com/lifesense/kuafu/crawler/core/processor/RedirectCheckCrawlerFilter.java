package com.lifesense.kuafu.crawler.core.processor;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProMessageCode;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.HttpConstant;

@CrawlerFilterTag(target = {"toutiao"}, priority = 12, type = CrawlerCommonConstants.FilterConstant.AFTER_FILTER_TYPE)
public class RedirectCheckCrawlerFilter implements ICrawlerFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectCheckCrawlerFilter.class);

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
