package com.dianping.merchant.robot.crawler.biz.filter.customer;

import com.lifesense.kuafu.crawler.encode.html.HtmlParserUtils;
import com.lifesense.kuafu.crawler.encode.tag.parser.TagParserEnum;
import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerFilterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import us.codecraft.webmagic.Page;

@CrawlerFilterTag(target = {"ifeng-tech"}, priority = 1, type = CrawlerCommonConstants.FilterConstant.PRE_FILTER_TYPE)
public class UrlParamConverterCrawlerFilter implements ICrawlerFilter {

    @Override
    public ProStatus doFilter(Page page) {
        String rowHtml = HtmlParserUtils.parser(page.getRawText(), TagParserEnum.a_remove_param);
        page.setRawText(rowHtml);
        return ProStatus.success();
    }
}
