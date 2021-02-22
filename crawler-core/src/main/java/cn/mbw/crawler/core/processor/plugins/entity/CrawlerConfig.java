package cn.mbw.crawler.core.processor.plugins.entity;

import cn.mbw.crawler.core.processor.plugins.proxy.DefaultSite;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CrawlerConfig implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7796710549377294662L;
    /**
     * 标注是哪个网站,默认为该json文件的文件名
     */
    private String domainTag;
    private String pageProcessor;
    private DefaultSite site = new DefaultSite();

    private Map<String, Object> customParams;

    private IUrlBuilder urlBuilder;

    private List<IFieldBuilder> fieldBuilder;

    private CrawlerBaseInfo crawlerBaseInfo;

    private List<CrawlerTrigger> crawlerTrigger;

    public Map<String, Object> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, Object> customParams) {
        this.customParams = customParams;
    }

    public String getDomainTag() {
        return domainTag;
    }

    public void setDomainTag(String domainTag) {
        this.domainTag = domainTag;
    }

    public String getPageProcessor() {
        return pageProcessor;
    }

    public void setPageProcessor(String pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    public CrawlerBaseInfo getCrawlerBaseInfo() {
        return crawlerBaseInfo;
    }

    public void setCrawlerBaseInfo(CrawlerBaseInfo crawlerBaseInfo) {
        this.crawlerBaseInfo = crawlerBaseInfo;
    }

    public IUrlBuilder getUrlBuilder() {
        return urlBuilder;
    }

    public void setUrlBuilder(IUrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    public List<IFieldBuilder> getFieldBuilder() {
        return fieldBuilder;
    }

    public void setFieldBuilder(List<IFieldBuilder> fieldBuilder) {
        this.fieldBuilder = fieldBuilder;
    }

    public DefaultSite getSite() {
        return site;
    }

    public void setSite(DefaultSite site) {
        this.site = site;
    }

    public List<CrawlerTrigger> getCrawlerTrigger() {
        return crawlerTrigger;
    }

    public void setCrawlerTrigger(List<CrawlerTrigger> crawlerTrigger) {
        this.crawlerTrigger = crawlerTrigger;
    }
}
