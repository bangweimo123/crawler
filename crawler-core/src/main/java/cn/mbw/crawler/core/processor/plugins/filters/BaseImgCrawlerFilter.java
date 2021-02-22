package cn.mbw.crawler.core.processor.plugins.filters;

import java.util.regex.Pattern;

import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import cn.mbw.crawler.core.processor.annotation.CrawlerFilterTag;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import cn.mbw.crawler.core.processor.iface.ICrawlerFilter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.UrlUtils;

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
