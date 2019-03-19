package com.dianping.merchant.robot.crawler.biz.filter.customer;

import us.codecraft.webmagic.Page;

import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerFilterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFilter;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.dianping.merchant.robot.crawler.encode.html.HtmlParserUtils;
import com.dianping.merchant.robot.crawler.encode.tag.parser.TagParserEnum;

@CrawlerFilterTag(target = {"ifeng-tech"}, priority = 1, type = CrawlerCommonConstants.FilterConstant.PRE_FILTER_TYPE)
public class UrlParamConverterCrawlerFilter implements ICrawlerFilter {

    @Override
    public ProStatus doFilter(Page page) {
        String rowHtml = HtmlParserUtils.parser(page.getRawText(), TagParserEnum.a_remove_param);
        page.setRawText(rowHtml);
        return ProStatus.success();
    }
}
