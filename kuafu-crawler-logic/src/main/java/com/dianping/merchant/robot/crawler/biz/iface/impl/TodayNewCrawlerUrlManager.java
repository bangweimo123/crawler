package com.dianping.merchant.robot.crawler.biz.iface.impl;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.news.background.api.dto.CrawlerURLDTO;
import com.dianping.merchant.news.background.api.service.URLService;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerUrlManager;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrl;

@Component
public class TodayNewCrawlerUrlManager implements ICrawlerUrlManager {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(TodayNewCrawlerUrlManager.class);

    @Resource
    private URLService todayNewUrlService;

    @Override
    public boolean isExistUrl(String url) {
        try {
            // 判断url存在并且已经成功
            return todayNewUrlService.isUrlExist(url);
        } catch (Exception e) {
            LOGGER.warn("error to isExistUrl:" + url, e);
        }
        return false;
    }

    @Override
    public boolean isSuccessUrl(String url) {
        CrawlerURLDTO urlDTO = todayNewUrlService.fetchURLDTOByURL(url);
        if (null == urlDTO) {
            return false;
        }
        boolean isBaseUrl = urlDTO.getUrl().equals(urlDTO.getBaseUrl());
        if (isBaseUrl) {
            return false;
        }
        if (ProMessageCode.SUCCESS.getCode() == urlDTO.getMessageCode()) {
            return true;
        }
        if (ProMessageCode.LOCK.getCode() == urlDTO.getMessageCode()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean addUrl(CrawlerUrl url) {
        try {
            CrawlerURLDTO crawlerUrlDTO = new CrawlerURLDTO();
            crawlerUrlDTO.setAddTime(new Date());
            crawlerUrlDTO.setBaseUrl(url.getBaseUrl());
            crawlerUrlDTO.setIsUpdate(url.getIsUpdate());
            crawlerUrlDTO.setMessageCode(url.getMessageCode());
            crawlerUrlDTO.setStatus(url.getStatus());
            crawlerUrlDTO.setUpdateTime(new Date());
            crawlerUrlDTO.setUrl(url.getUrl());
            if(MapUtils.isEmpty(url.getExtra())){
                url.setExtra(new HashMap<String,Object>());
            }
            crawlerUrlDTO.setExtras(url.getExtra());
            return todayNewUrlService.addURL(crawlerUrlDTO);
        } catch (Exception e) {
            LOGGER.warn("error to addUrl:" + url, e);
        }
        return false;
    }

    @Override
    public CrawlerUrl getByUrl(String url) {
        try {
            if (StringUtils.isBlank(url)) {
                return null;
            }
            CrawlerURLDTO urlDTO = todayNewUrlService.fetchURLDTOByURL(url);
            CrawlerUrl crawlerUrl = new CrawlerUrl();
            crawlerUrl.setBaseUrl(urlDTO.getBaseUrl());
            crawlerUrl.setExtra(urlDTO.getExtras());
            crawlerUrl.setIsUpdate(urlDTO.getIsUpdate());
            crawlerUrl.setMessageCode(urlDTO.getMessageCode());
            crawlerUrl.setStatus(urlDTO.getStatus());
            crawlerUrl.setUrl(urlDTO.getUrl());
            return crawlerUrl;
        } catch (Exception e) {
            LOGGER.warn("error to getByUrl:" + url, e);
        }
        return null;
    }

    @Override
    public boolean updateUrl(CrawlerUrl url) {
        try {
            CrawlerURLDTO existUrl = todayNewUrlService.fetchURLDTOByURL(url.getUrl());
            Assert.notNull(existUrl, "url不存在");
            existUrl.setUpdateTime(new Date());
            existUrl.setStatus(url.getStatus());
            existUrl.setMessageCode(url.getMessageCode());
            existUrl.setBaseUrl(url.getBaseUrl());
            if(MapUtils.isNotEmpty(url.getExtra())){
                existUrl.getExtras().putAll(url.getExtra());
            }
            return todayNewUrlService.updateURL(existUrl);
        } catch (Exception e) {
            LOGGER.warn("error to updateUrl:" + url, e);
        }
        return false;
    }
}
