package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import cn.mbw.crawler.core.processor.iface.ICrawlerFieldHandler;
import cn.mbw.crawler.core.processor.iface.impl.BaseCrawlerFieldHandler;
import cn.mbw.crawler.core.processor.plugins.entity.CommonUrlBuilder;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import cn.mbw.crawler.core.processor.plugins.entity.ProcessorContext;
import org.codehaus.plexus.util.StringUtils;
import us.codecraft.webmagic.Page;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PagePrcoessor("boohee")
public class BooheePageProcessor extends CommonPageProcessor {

    private ICrawlerFieldHandler fieldHandler = new BaseCrawlerFieldHandler();

    public BooheePageProcessor(CrawlerConfig config) {
        super(config);
    }

    public void process(Page page) {
        ProcessorContext.getContext(page)

                .addParam(CrawlerCommonConstants.ProcessorContextConstant.BASE_URL, getConfig().getCrawlerBaseInfo().getBaseUrl())

                .addParam(CrawlerCommonConstants.ProcessorContextConstant.CURRENT_URL, page.getRequest().getUrl())

                .addParam(CrawlerCommonConstants.ProcessorContextConstant.DOMAIN_TAG, getConfig().getDomainTag())

                .addParam(CrawlerCommonConstants.ProcessorContextConstant.LIMIT_MONTH, getConfig().getCrawlerBaseInfo().getLimitMonth())

                .addParam(CrawlerCommonConstants.ProcessorContextConstant.CUSTOM_PARAMS, getConfig().getCustomParams());
        String currentUrl = page.getRequest().getUrl();
        String baseUrlFilter = getConfig().getUrlBuilder().getBaseUrlFilter();

        if (currentUrl.matches(baseUrlFilter)) {
            ProStatus proStatus = ProStatus.success();
            Pattern pattern = Pattern.compile(baseUrlFilter);
            Matcher matcher = pattern.matcher(currentUrl);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                String indexStr = matcher.group(2);
                if (StringUtils.isNotBlank(indexStr)) {
                    int index = Integer.parseInt(indexStr);
                    boolean needNextHandler = true;
                    Map<String, Object> customParams = (Map<String, Object>) ProcessorContext.getContext(page).getParam(CrawlerCommonConstants.ProcessorContextConstant.CUSTOM_PARAMS);
                    if (null != customParams.get("start") && null != customParams.get("size")) {
                        Integer start = (Integer) customParams.get("start");
                        Integer size = (Integer) customParams.get("size");
                        if (index >= (start + size)) {
                            needNextHandler = false;
                        }
                    }
                    if (needNextHandler) {
                        //设置id
                        page.putField("_id", index);
                        index++;
                        String nextUrl = prefix + index;
                        processNextPage(page, nextUrl);
                        // 页面field提取
                        proStatus = fieldHandler.pageField(page, getConfig().getFieldBuilder());
                        ProcessorContext.getContext(page).setProStatus(proStatus);
                    } else {
                        proStatus = ProStatus.fail(1001);
                        ProcessorContext.getContext(page).setProStatus(proStatus);
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
        newUrlBuilder.setMethod(getConfig().getUrlBuilder().getMethod());
        newUrlBuilder.setBaseDeep(getConfig().getUrlBuilder().getBaseDeep());
        newUrlBuilder.setBaseCount(getConfig().getUrlBuilder().getBaseCount());
        newUrlBuilder.setBaseUrlFilter(getConfig().getUrlBuilder().getBaseUrlFilter());
        newUrlBuilder.setPageUrlFilterPlugins(getConfig().getUrlBuilder().getPageUrlFilterPlugins());
        newUrlBuilder.setUrlFilterPlugins(getConfig().getUrlBuilder().getUrlFilterPlugins());
        newUrlBuilder.setCurrentUrl(page.getRequest().getUrl());
        // 定义一个附加参数
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME, newUrlBuilder);
        super.getUrlHandler().addTargetRequestWithoutCanno(page, nextUrl, extras, getConfig().getUrlBuilder().getMethod());
    }
}
