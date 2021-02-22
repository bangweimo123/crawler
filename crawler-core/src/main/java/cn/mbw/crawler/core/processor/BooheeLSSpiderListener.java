package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import cn.mbw.crawler.core.processor.iface.ICrawlerUrlHandler;
import cn.mbw.crawler.core.processor.iface.ICrawlerUrlManager;
import cn.mbw.crawler.core.processor.iface.impl.BaseCrawlerUrlHandler;
import cn.mbw.crawler.core.processor.plugins.entity.CommonUrlBuilder;
import cn.mbw.crawler.core.processor.spider.LSSpider;
import cn.mbw.crawler.core.processor.spider.LSSpiderListener;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.selector.PlainText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooheeLSSpiderListener extends LSSpiderListener {


    private ICrawlerUrlHandler urlHandler = new BaseCrawlerUrlHandler();
    private CrawlerConfig config;

    public BooheeLSSpiderListener(CrawlerConfig config, Scheduler scheduler, LSSpider spider, ICrawlerUrlManager crawlerUrlManager) {
        super(scheduler, spider, crawlerUrlManager);
        this.config = config;
    }

    @Override
    public void onError(Request request) {
        String currentUrl = request.getUrl();
        Page page = new Page();
        page.setRawText("");
        page.setUrl(new PlainText(currentUrl));
        page.setRequest(request);
        page.setStatusCode(200);
        String baseUrlFilter = config.getUrlBuilder().getBaseUrlFilter();
        if (currentUrl.matches(baseUrlFilter)) {
            Pattern pattern = Pattern.compile(baseUrlFilter);
            Matcher matcher = pattern.matcher(currentUrl);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                String indexStr = matcher.group(2);
                if (StringUtils.isNotBlank(indexStr)) {
                    int index = Integer.parseInt(indexStr);
                    index++;
                    String nextUrl = prefix + index;
                    boolean needNextHandler = true;
                    Map<String, Object> customParams = config.getCustomParams();
                    if (null != customParams.get("start") && null != customParams.get("size")) {
                        Integer start = (Integer) customParams.get("start");
                        Integer size = (Integer) customParams.get("size");
                        if (index >= (start + size)) {
                            needNextHandler = false;
                        }
                    }
                    if (needNextHandler) {
                        processNextPage(page, nextUrl);
                        getSpider().extractAndAddRequests(page, getSpider().isSpawnUrl());
                    }
                }
            }
        }

    }

    protected void processNextPage(Page page, String nextUrl) {
        CommonUrlBuilder newUrlBuilder = null;
        Object extraUrlBuilder = page.getRequest().getExtra(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME);
        if (null == newUrlBuilder) {
            newUrlBuilder = new CommonUrlBuilder();
        } else {
            if (extraUrlBuilder instanceof CommonUrlBuilder) {
                newUrlBuilder = (CommonUrlBuilder) extraUrlBuilder;
            } else {
                JSONObject jsonObject = (JSONObject) extraUrlBuilder;
                newUrlBuilder = JSON.toJavaObject(jsonObject, CommonUrlBuilder.class);
            }
        }
        newUrlBuilder.setMethod(config.getUrlBuilder().getMethod());
        newUrlBuilder.setBaseDeep(config.getUrlBuilder().getBaseDeep());
        newUrlBuilder.setBaseCount(config.getUrlBuilder().getBaseCount());
        newUrlBuilder.setBaseUrlFilter(config.getUrlBuilder().getBaseUrlFilter());
        newUrlBuilder.setPageUrlFilterPlugins(config.getUrlBuilder().getPageUrlFilterPlugins());
        newUrlBuilder.setUrlFilterPlugins(config.getUrlBuilder().getUrlFilterPlugins());
        newUrlBuilder.setCurrentUrl(page.getRequest().getUrl());
        // 定义一个附加参数
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME, newUrlBuilder);
        urlHandler.addTargetRequestWithoutCanno(page, nextUrl, extras, config.getUrlBuilder().getMethod());
    }
}
