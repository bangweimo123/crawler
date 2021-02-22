package cn.mbw.crawler.core.processor.iface.impl;

import cn.mbw.crawler.core.processor.iface.ICrawlerUrlHandler;
import cn.mbw.crawler.core.processor.plugins.entity.*;
import cn.mbw.crawler.core.processor.spider.SpiderFactory;
import cn.mbw.crawler.core.processor.utils.RedisCountUtils;
import cn.mbwcrawler.core.processor.plugins.entity.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基本的url处理器
 *
 * @author mobangwei
 */
public class BaseCrawlerUrlHandler implements ICrawlerUrlHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCrawlerUrlHandler.class);

    @Override
    public ProStatus handler(Page page, IUrlBuilder urlBuilder) {
        ProcessorContext context = ProcessorContext.getContext(page);

        CommonUrlBuilder newUrlBuilder = null;
        Object obj = page.getRequest().getExtra(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME);
        if (obj instanceof JSONObject) {
            try {
                newUrlBuilder = JSON.parseObject(((JSONObject) obj).toJSONString(), CommonUrlBuilder.class);
            } catch (Exception e) {
                LOGGER.warn("exception for parseException", e);
            }
        } else {
            newUrlBuilder = (CommonUrlBuilder) obj;
        }
        if (null == newUrlBuilder) {
            newUrlBuilder = new CommonUrlBuilder();
        }
        newUrlBuilder.setMethod(urlBuilder.getMethod());
        newUrlBuilder.setBaseDeep(urlBuilder.getBaseDeep());
        newUrlBuilder.setBaseCount(urlBuilder.getBaseCount());
        newUrlBuilder.setBaseUrlFilter(urlBuilder.getBaseUrlFilter());
        newUrlBuilder.setPageUrlFilterPlugins(urlBuilder.getPageUrlFilterPlugins());
        newUrlBuilder.setUrlFilterPlugins(urlBuilder.getUrlFilterPlugins());
        newUrlBuilder.setCurrentUrl(page.getRequest().getUrl());

        // 定义一个附加参数
        Map<String, Object> extras = new HashMap<String, Object>();

        // 如果有深度控制
        if (null != urlBuilder.getBaseDeep()) {
            if (null == newUrlBuilder.getCurrentDeep()) {
                newUrlBuilder.setCurrentDeep(1);
            } else {
                newUrlBuilder.setCurrentDeep(newUrlBuilder.getCurrentDeep() + 1);
            }
            if (newUrlBuilder.getCurrentDeep() >= newUrlBuilder.getBaseDeep()) {
                return ProStatus.fail(ProMessageCode.URL_PARSE_ERROR.getCode());
            }
        }

        // 如果有数量控制
        if (null != newUrlBuilder.getBaseCount()) {
            String domainTag = (String) ProcessorContext.getContext(page).getParam("domainTag");
            Integer currentCount = RedisCountUtils.incrAndReturn(domainTag);
            newUrlBuilder.setCurrentCount(currentCount);
            if (newUrlBuilder.getCurrentCount() >= newUrlBuilder.getBaseCount()) {
                SpiderFactory.destroySpider(domainTag);
                return ProStatus.fail(ProMessageCode.URL_PARSE_ERROR.getCode());
            }
            LOGGER.info("current count :" + newUrlBuilder.getCurrentCount() + ",current url:" + newUrlBuilder.getCurrentUrl());
        }
        Selectable linkSelectable = page.getHtml().links();
        if (StringUtils.isNotBlank(newUrlBuilder.getBaseUrlFilter())) {
            linkSelectable = linkSelectable.regex(newUrlBuilder.getBaseUrlFilter());
        }
        // 此为对当前页面的一个特殊处理，即判断是否满足特定的regex,如果满足,则需要做多重过滤
        if (MapUtils.isNotEmpty(newUrlBuilder.getUrlFilterPlugins())) {
            for (String urlRegex : newUrlBuilder.getUrlFilterPlugins().keySet()) {
                boolean currentUrlMatch = newUrlBuilder.getCurrentUrl().matches(urlRegex);

                // 如果当前页面满足
                if (currentUrlMatch) {
                    String regexFilter = newUrlBuilder.getUrlFilterPlugins().get(urlRegex);
                    if (StringUtils.isNotBlank(regexFilter)) {
                        linkSelectable = linkSelectable.regex(regexFilter);
                    } else {
                        LOGGER.error("error to get regex filter");
                    }
                    List<String> allLinkes = linkSelectable.all();
                    if (CollectionUtils.isNotEmpty(allLinkes)) {
                        //TODO urlrewrite
                        for (String link : allLinkes) {
                            link.replace("/item", "");
                        }
                        extras.put(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME, newUrlBuilder);
                        addTargetRequests(page, allLinkes, extras, urlBuilder.getMethod());
                    }
                    return ProStatus.fail(ProMessageCode.URL_DONT_NEED_PARSE.getCode());
                }
            }
        }

        // 针对pageGroup做匹配
        String pageGroup = (String) page.getRequest().getExtra(CrawlerCommonConstants.PageGroupConstant.PAGE_GROUP);
        String pageGroupRegex = (String) page.getRequest().getExtra(CrawlerCommonConstants.PageGroupConstant.PAGE_GROUP_REGEX);
        Integer pageGroupIndex = null;
        if (StringUtils.isNotBlank(pageGroup)) {
            if (StringUtils.isNotBlank(pageGroupRegex)) {
                LOGGER.info("matcher for pageGroup:" + pageGroup + "|" + newUrlBuilder.getCurrentUrl());
                Pattern pattern = Pattern.compile(pageGroupRegex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(newUrlBuilder.getCurrentUrl());
                while (matcher.find()) {
                    String index = matcher.group(matcher.groupCount());
                    pageGroupIndex = Integer.parseInt(index);
                    break;
                }
            }
        }
        if (MapUtils.isNotEmpty(newUrlBuilder.getPageUrlFilterPlugins())) {
            for (String urlRegex : newUrlBuilder.getPageUrlFilterPlugins().keySet()) {
                String regexFilter = newUrlBuilder.getPageUrlFilterPlugins().get(urlRegex);
                boolean currentUrlMatch = newUrlBuilder.getCurrentUrl().matches(urlRegex);
                // 如果当前页面满足,表示为详情页面的第一页
                if (currentUrlMatch) {
                    pageGroupRegex = new String(regexFilter);
                }
            }
        }
        // 重置pageGroupIndex,这步很重要
        if (null == pageGroupIndex) {
            pageGroupIndex = 1;
            LOGGER.warn("to parse pageGroup to currentUrl:" + pageGroup + ":" + newUrlBuilder.getCurrentUrl());
            pageGroup = newUrlBuilder.getCurrentUrl();
        }
        if (StringUtils.isNotBlank(pageGroup)) {
            context.addParam(CrawlerCommonConstants.PageGroupConstant.PAGE_GROUP, pageGroup);
            context.addParam(CrawlerCommonConstants.PageGroupConstant.PAGE_GROUP_INDEX, pageGroupIndex);
            // 添加一个pagegroup字段
            extras.put(CrawlerCommonConstants.PageGroupConstant.PAGE_GROUP, pageGroup);
            extras.put(CrawlerCommonConstants.PageGroupConstant.PAGE_GROUP_REGEX, pageGroupRegex);
        }


        List<String> allLinkes = linkSelectable.all();
        if (CollectionUtils.isNotEmpty(allLinkes)) {
            extras.put(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME, newUrlBuilder);
            addTargetRequests(page, allLinkes, extras, newUrlBuilder.getMethod());
        }
        return ProStatus.success();
    }

    @Override
    public void addTargetRequests(Page page, List<String> requestStrings, Map<String, Object> extras, String method) {
        synchronized (page.getTargetRequests()) {
            for (String s : requestStrings) {
                if (StringUtils.isBlank(s) || s.equals("#")) {
                    return;
                }
                s = UrlUtils.canonicalizeUrl(s, page.getUrl().toString());
                Request request = new Request(s);
                if (StringUtils.isNotEmpty(method)) {
                    if (HttpConstant.Method.POST.equalsIgnoreCase(method)) {
                        extras.put(CrawlerCommonConstants.RequestParamConstant.NAME_VALUE_PAIR, new BasicNameValuePair[]{});
                    }
                    request.setMethod(method);
                }
                request.setExtras(extras);
                page.getTargetRequests().add(request);
            }
        }
    }

    /**
     * add url to fetch
     *
     * @param requestString
     */
    @SuppressWarnings("unused")
    @Override
    public void addTargetRequest(Page page, String requestString, Map<String, Object> extras, String method) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        synchronized (page.getTargetRequests()) {
            requestString = UrlUtils.canonicalizeUrl(requestString, page.getUrl().toString());
            Request request = new Request(requestString);
            if (StringUtils.isNotEmpty(method)) {
                if (HttpConstant.Method.POST.equalsIgnoreCase(method)) {
                    extras.put(CrawlerCommonConstants.RequestParamConstant.NAME_VALUE_PAIR, new BasicNameValuePair[]{});
                }
                request.setMethod(method);
            }
            request.setExtras(extras);
            page.getTargetRequests().add(request);
        }
    }

    @Override
    public void addTargetRequestWithoutCanno(Page page, String requestString, Map<String, Object> extras, String method) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        synchronized (page.getTargetRequests()) {
            Request request = new Request(requestString);
            if (StringUtils.isNotEmpty(method)) {
                if (HttpConstant.Method.POST.equalsIgnoreCase(method)) {
                    extras.put(CrawlerCommonConstants.RequestParamConstant.NAME_VALUE_PAIR, new BasicNameValuePair[]{});
                }
                request.setMethod(method);
            }
            request.setExtras(extras);
            page.getTargetRequests().add(request);
        }
    }
}
