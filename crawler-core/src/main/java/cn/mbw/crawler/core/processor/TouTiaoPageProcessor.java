package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.plugins.entity.CommonUrlBuilder;
import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import cn.mbw.crawler.core.processor.plugins.entity.ProcessorContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TouTiaoPageProcessor extends CommonPageProcessor {
    public TouTiaoPageProcessor(CrawlerConfig config) {
        super(config);
    }

    public void process(Page page) {
        Boolean isJSONPage = false;
        if (page.getRequest() != null) {
            Object result = page.getRequest().getExtra("isJSONPage");
            if (null != result) {
                isJSONPage = (Boolean) result;
            }
        }
        if (isJSONPage) {
            ProcessorContext.getContext(page)

                    .addParam(CrawlerCommonConstants.ProcessorContextConstant.BASE_URL, getConfig().getCrawlerBaseInfo().getBaseUrl())

                    .addParam(CrawlerCommonConstants.ProcessorContextConstant.CURRENT_URL, page.getRequest().getUrl())

                    .addParam(CrawlerCommonConstants.ProcessorContextConstant.DOMAIN_TAG, getConfig().getDomainTag())

                    .addParam(CrawlerCommonConstants.ProcessorContextConstant.LIMIT_MONTH, getConfig().getCrawlerBaseInfo().getLimitMonth());
            ProStatus proStatus = ProStatus.success();
            if (proStatus.isSuccess()) {
                String jsonContent = page.getRawText();
                JSONObject jsonObject = JSON.parseObject(jsonContent);
                processNextPage(page, jsonObject);
                processData(page, jsonObject);
                ProcessorContext.getContext(page).setProStatus(proStatus);
            }
        } else {
            super.process(page);
        }
    }

    private void processNextPage(Page page, JSONObject jsonObject) {
        Integer hasMore = jsonObject.getInteger("has_more");
        if (hasMore == 1) {
            JSONObject next = jsonObject.getJSONObject("next");
            Long max_behot_time = next.getLong("max_behot_time");
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("max_behot_time", max_behot_time);
            String pageUrl = page.getUrl().toString();
            Pattern pattern = Pattern.compile("toutiao-user:(\\d+)\\|toutiao-mid:(\\d+)\\|serialNumber:(\\d+)");
            Matcher matcher = pattern.matcher(pageUrl);
            if (matcher.matches()) {
                String user = matcher.group(1);
                String mid = matcher.group(2);
                String serialNumber = matcher.group(3);
                String nextSerialNumber = Integer.toString(Integer.parseInt(serialNumber) + 1);
                pageUrl = "toutiao-user:" + user + "|toutiao-mid:" + mid + "|serialNumber:" + nextSerialNumber;
            }
            super.getUrlHandler().addTargetRequestWithoutCanno(page, pageUrl, extras, getConfig().getUrlBuilder().getMethod());

        }
    }

    private void processData(Page page, JSONObject jsonObject) {
        JSONArray data = jsonObject.getJSONArray("data");
        if (CollectionUtils.isNotEmpty(data)) {
            CommonUrlBuilder newUrlBuilder = null;
            newUrlBuilder = (CommonUrlBuilder) page.getRequest().getExtra(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME);
            if (null == newUrlBuilder) {
                newUrlBuilder = new CommonUrlBuilder();
            }
            newUrlBuilder.setMethod(getConfig().getUrlBuilder().getMethod());
            newUrlBuilder.setBaseDeep(getConfig().getUrlBuilder().getBaseDeep());
            newUrlBuilder.setBaseCount(getConfig().getUrlBuilder().getBaseCount());
            newUrlBuilder.setBaseUrlFilter(getConfig().getUrlBuilder().getBaseUrlFilter());
            newUrlBuilder.setPageUrlFilterPlugins(getConfig().getUrlBuilder().getPageUrlFilterPlugins());
            newUrlBuilder.setUrlFilterPlugins(getConfig().getUrlBuilder().getUrlFilterPlugins());
            newUrlBuilder.setCurrentUrl(page.getRequest().getUrl());
            for (int i = 0; i < data.size(); i++) {
                JSONObject item = data.getJSONObject(i);
                String url = item.getString("seo_url");
                // 定义一个附加参数
                Map<String, Object> extras = new HashMap<String, Object>();
                String keywords = item.getString("keywords");
                String source = item.getString("source");
                String title = item.getString("title");
                String datetime = item.getString("datetime");
                JSONArray thumbImages = item.getJSONArray("thumb_image");
                String imageUrl = item.getString("image_url");
                Map<String, Object> relationData = new HashMap<>();
                relationData.put("keywords", keywords);
                relationData.put("addTime", datetime);
                relationData.put("source", source);
                relationData.put("title", title);
                relationData.put("thumbImages", thumbImages);
                if (CollectionUtils.isNotEmpty(thumbImages)) {
                    relationData.put("imageUrl", thumbImages.getJSONObject(0).getString("url"));
                }
                extras.put(CrawlerCommonConstants.URLBuilderConstant.URL_BUILDER_NAME, newUrlBuilder);
                extras.put(CrawlerCommonConstants.URLBuilderConstant.RELATION_DATA, relationData);
                super.getUrlHandler().addTargetRequestWithoutCanno(page, url, extras, getConfig().getUrlBuilder().getMethod());

            }
        }
    }
}
