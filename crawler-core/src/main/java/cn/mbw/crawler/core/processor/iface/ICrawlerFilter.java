package cn.mbw.crawler.core.processor.iface;

import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import us.codecraft.webmagic.Page;

/**
 * 过滤器，对于爬取的内容进行过滤,相当于一个拦截器的功能
 *
 * @author mobangwei
 *
 */
public interface ICrawlerFilter {
    /**
     * 针对页面分析的一些情况进行过滤
     *
     * @param page
     * @return
     */
    ProStatus doFilter(Page page);
}
