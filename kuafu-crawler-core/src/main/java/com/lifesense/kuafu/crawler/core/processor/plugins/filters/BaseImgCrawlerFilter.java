package com.lifesense.kuafu.crawler.core.processor.plugins.filters;

import java.util.regex.Pattern;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.UrlUtils;

import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;

/**
 * 将不带协议的图片地址添加上domain
 * 
 * @author mobangwei
 * 
 */
@CrawlerFilterTag(priority = 5, type = CrawlerCommonConstants.FilterConstant.PRE_FILTER_TYPE)
public class BaseImgCrawlerFilter implements ICrawlerFilter {

    @Override
    public ProStatus doFilter(Page page) {
        Pattern pattern = Pattern.compile("(<img[^<>]*src=)[\"']([^\"'<>]*)[\"']", Pattern.CASE_INSENSITIVE);
        String rowHtml = UrlUtils.replaceByPattern(page.getRawText(), page.getRequest().getUrl(), pattern);
        page.setRawText(rowHtml);
        return ProStatus.success();
    }
}
