package com.dianping.merchant.robot.crawler.common.processor.plugins.filters;

import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.UrlUtils;

import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerFilterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFilter;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;

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
