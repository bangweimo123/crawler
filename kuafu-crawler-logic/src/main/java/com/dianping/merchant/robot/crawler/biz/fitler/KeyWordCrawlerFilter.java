package com.dianping.merchant.robot.crawler.biz.fitler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import us.codecraft.webmagic.Page;

import com.dianping.merchant.news.background.api.external.dto.CrawlerKeywordTagItemDTO;
import com.dianping.merchant.news.background.api.external.dto.CrawlerKeywordTagsDTO;
import com.dianping.merchant.news.background.api.external.service.KeywordExternalService;
import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerFilterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFilter;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProcessorContext;
import com.google.common.collect.Lists;

/**
 * 关键词过滤
 * 
 * @author mobangwei
 * 
 */
//@CrawlerFilterTag(priority = 2, type = CrawlerCommonConstants.FilterConstant.AFTER_FILTER_TYPE)
public abstract class KeyWordCrawlerFilter implements ICrawlerFilter {

    private static Map<String, CrawlerKeywordTagsDTO> keywordCacheMap = new HashMap<String, CrawlerKeywordTagsDTO>();
    @Resource
    private KeywordExternalService todayNewKeywordService;

    private void initCacheMap() {
        List<CrawlerKeywordTagsDTO> crawlerKeyWords = todayNewKeywordService.queryAllKeywords();
        for (CrawlerKeywordTagsDTO keyWordDTO : crawlerKeyWords) {
            keywordCacheMap.put(keyWordDTO.getKeyword(), keyWordDTO);
        }
    }

    private synchronized Map<String, CrawlerKeywordTagsDTO> getCache() {
        if (MapUtils.isEmpty(keywordCacheMap)) {
            initCacheMap();
        }
        return keywordCacheMap;
    }

    private List<Integer> parseTagIds(List<CrawlerKeywordTagItemDTO> tagItemDTOs) {
        List<Integer> tagIds = new ArrayList<Integer>();
        if (CollectionUtils.isNotEmpty(tagItemDTOs)) {
            for (CrawlerKeywordTagItemDTO tagItemDTO : tagItemDTOs) {
                tagIds.add(tagItemDTO.getTagId());
            }
        }
        return tagIds;
    }

    abstract protected String getXpath();

    abstract protected String getSplit();

    @Override
    public ProStatus doFilter(Page page) {
        // 获取网站的关键词
        List<String> keywords = page.getResultItems().get("keywords");
        if (CollectionUtils.isNotEmpty(keywords)) {
            Map<String, CrawlerKeywordTagsDTO> keyWordCache = getCache();
            Set<Integer> tagIds = new HashSet<Integer>();
            for (String keyword : keywords) {
                if (keyWordCache.containsKey(keyword)) {
                    CrawlerKeywordTagsDTO keyWordDTO = keyWordCache.get(keyword);
                    tagIds.addAll(parseTagIds(keyWordDTO.getTags()));
                }
            }
            // 给这个文件打上标签
            if (CollectionUtils.isNotEmpty(tagIds)) {
                ProcessorContext.getContext(page).addParam("tagIds", Lists.newArrayList(tagIds));
                return ProStatus.success();
            }
        }
        return ProStatus.fail(ProMessageCode.FITLER_KEYWORD_ERROR.getCode());
    }
}
