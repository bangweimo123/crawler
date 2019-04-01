package com.lifesense.kuafu.crawler.core.processor;

import java.util.List;

import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFieldHandler;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerUrlHandler;
import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants.ProcessorContextConstant;
import com.lifesense.kuafu.crawler.core.processor.iface.impl.BaseCrawlerFieldHandler;
import com.lifesense.kuafu.crawler.core.processor.iface.impl.BaseCrawlerUrlHandler;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.CrawlerConfig;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProcessorContext;
import com.lifesense.kuafu.crawler.core.processor.plugins.filters.CrawlerFilterFactory;

/**
 * 一个公用的page Processor
 *
 * @author mobangwei
 */
public class CommonPageProcessor implements PageProcessor {

    private CrawlerConfig config;
    /**
     * 刚处理时的过滤
     */
    private List<ICrawlerFilter> preCrawlerFilters;
    /**
     * url处理和field处理之间的过滤
     */
    private List<ICrawlerFilter> middleCrawlerFilters;
    /**
     * 处理完成后的过滤
     */
    private List<ICrawlerFilter> afterCrawlerFilters;

    private ICrawlerFieldHandler fieldHandler = new BaseCrawlerFieldHandler();

    private ICrawlerUrlHandler urlHandler = new BaseCrawlerUrlHandler();

    public CommonPageProcessor(CrawlerConfig config) {
        this.config = config;
        setPreCrawlerFilters(CrawlerFilterFactory.getPreFilters(config.getDomainTag()));
        setMiddleCrawlerFilters(CrawlerFilterFactory.getMiddleFilters(config.getDomainTag()));
        setAfterCrawlerFilters(CrawlerFilterFactory.getAfterFilters(config.getDomainTag()));
    }

    public CrawlerConfig getConfig() {
        return config;
    }

    public void setConfig(CrawlerConfig config) {
        this.config = config;
    }

    public List<ICrawlerFilter> getPreCrawlerFilters() {
        return preCrawlerFilters;
    }

    public void setPreCrawlerFilters(List<ICrawlerFilter> preCrawlerFilters) {
        this.preCrawlerFilters = preCrawlerFilters;
    }

    public List<ICrawlerFilter> getMiddleCrawlerFilters() {
        return middleCrawlerFilters;
    }

    public void setMiddleCrawlerFilters(List<ICrawlerFilter> middleCrawlerFilters) {
        this.middleCrawlerFilters = middleCrawlerFilters;
    }

    public List<ICrawlerFilter> getAfterCrawlerFilters() {
        return afterCrawlerFilters;
    }

    public void setAfterCrawlerFilters(List<ICrawlerFilter> afterCrawlerFilters) {
        this.afterCrawlerFilters = afterCrawlerFilters;
    }

    public ICrawlerFieldHandler getFieldHandler() {
        return fieldHandler;
    }

    public void setFieldHandler(ICrawlerFieldHandler fieldHandler) {
        this.fieldHandler = fieldHandler;
    }

    public ICrawlerUrlHandler getUrlHandler() {
        return urlHandler;
    }

    public void setUrlHandler(ICrawlerUrlHandler urlHandler) {
        this.urlHandler = urlHandler;
    }

    @Override
    public void process(Page page) {
        ProcessorContext.getContext(page)

                .addParam(ProcessorContextConstant.BASE_URL, config.getCrawlerBaseInfo().getBaseUrl())

                .addParam(ProcessorContextConstant.CURRENT_URL, page.getRequest().getUrl())

                .addParam(ProcessorContextConstant.DOMAIN_TAG, config.getDomainTag())

                .addParam(ProcessorContextConstant.LIMIT_MONTH, config.getCrawlerBaseInfo().getLimitMonth())

                .addParam(ProcessorContextConstant.CUSTOM_PARAMS, config.getCustomParams());
        ProStatus proStatus = ProStatus.success();


        if (proStatus.isSuccess()) {
            // 页前过滤
            if (CollectionUtils.isNotEmpty(preCrawlerFilters)) {
                for (ICrawlerFilter filter : preCrawlerFilters) {
                    proStatus = filter.doFilter(page);
                    if (!proStatus.isSuccess()) {
                        break;
                    }
                }
            }
        }
        // 先进行地址过滤,生成我们需要的url
        if (proStatus.isSuccess()) {
            // 地址处理
            proStatus = urlHandler.handler(page, config.getUrlBuilder());
        }
        if (proStatus.isSuccess()) {
            // 页前过滤
            if (CollectionUtils.isNotEmpty(middleCrawlerFilters)) {
                for (ICrawlerFilter filter : middleCrawlerFilters) {
                    proStatus = filter.doFilter(page);
                    if (!proStatus.isSuccess()) {
                        break;
                    }
                }
            }
        }
        if (proStatus.isSuccess()) {
            //主链接不做相应的field处理
            if (!StringUtils.equalsIgnoreCase(page.getUrl().get(), config.getCrawlerBaseInfo().getBaseUrl())) {
                // 页面field提取
                proStatus = fieldHandler.pageField(page, config.getFieldBuilder());
            }
        }
        if (proStatus.isSuccess()) {
            // 处理后的过滤
            if (CollectionUtils.isNotEmpty(afterCrawlerFilters)) {
                for (ICrawlerFilter filter : afterCrawlerFilters) {
                    proStatus = filter.doFilter(page);
                    if (!proStatus.isSuccess()) {
                        break;
                    }
                }
            }
        }
        ProcessorContext.getContext(page).setProStatus(proStatus);

    }

    @Override
    public Site getSite() {
        return config.getSite().parse();
    }
}
