package com.lifesense.kuafu.crawler.core.processor;


import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * A simple PageProcessor.
 * 
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class TestPageProcessor implements PageProcessor {

    private String urlPattern;

    private Site site;


    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public TestPageProcessor(String startUrl, String charset, String urlPattern) {
        this.site = Site.me().addStartUrl(startUrl).setDomain(UrlUtils.getDomain(startUrl)).setCharset(charset);
        // compile "*" expression to regex
        this.urlPattern = "(" + urlPattern.replace(".", "\\.").replace("*", "[^\"'#]*") + ")";

    }

    @Override
    public void process(Page page) {
        List<String> requests = page.getHtml().links().regex(urlPattern).all();
        // add urls to fetch
        page.addTargetRequests(requests);
        // extract by XPath
        page.putField("title", page.getHtml().xpath("//title"));
        page.putField("html", page.getHtml().toString());
        // extract by Readability
        page.putField("content", page.getHtml().smartContent());
    }

    @Override
    public Site getSite() {
        // settings
        return site;
    }

}
