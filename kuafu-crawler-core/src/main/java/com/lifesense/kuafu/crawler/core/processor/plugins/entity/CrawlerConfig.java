package com.lifesense.kuafu.crawler.core.processor.plugins.entity;

import java.io.Serializable;
import java.util.List;

public class CrawlerConfig implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7796710549377294662L;
    /**
     * 标注是哪个网站,默认为该json文件的文件名
     */
    private String domainTag;

    private DefaultSite site = new DefaultSite();

    private IUrlBuilder urlBuilder;

    private List<IFieldBuilder> fieldBuilder;

    private CrawlerBaseInfo crawlerBaseInfo;

    private List<CrawlerTrigger> crawlerTrigger;


    public String getDomainTag() {
        return domainTag;
    }

    public void setDomainTag(String domainTag) {
        this.domainTag = domainTag;
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
