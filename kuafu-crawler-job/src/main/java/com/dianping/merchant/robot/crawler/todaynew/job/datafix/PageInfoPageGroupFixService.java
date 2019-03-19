package com.dianping.merchant.robot.crawler.todaynew.job.datafix;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.core.type.PageModel;
import com.dianping.merchant.news.background.api.constants.StatusEnum;
import com.dianping.merchant.news.background.api.dto.CrawlerPageInfoDTO;
import com.dianping.merchant.news.background.api.service.PageInfoService;

@Service("fix_page_info")
public class PageInfoPageGroupFixService implements IDataFixService {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(PageInfoPageGroupFixService.class);
    @Resource
    private PageInfoService pageInfoService;

    private static final Integer LIMIT_MAX = 20;

    private static ExecutorService es = Executors.newFixedThreadPool(20);

    @Override
    public void fix(Map<String, Object> params) {
        Integer limit = Integer.parseInt((String) params.get("limit"));
        Integer offset = Integer.parseInt((String) params.get("offset"));
        while (limit >= LIMIT_MAX) {
            doHandler(offset, LIMIT_MAX);
            offset = offset + LIMIT_MAX;
            limit = limit - LIMIT_MAX;
        }
        if (limit > 0) {
            doHandler(offset, limit);
        }
    }

    private void doHandler(Integer offset, Integer limit) {
        LOGGER.info("offset,limit:" + offset + "," + limit);
        PageModel resultData = pageInfoService.queryPageModel(StatusEnum.valid, limit, offset);
        List<CrawlerPageInfoDTO> resultList = (List<CrawlerPageInfoDTO>) resultData.getRecords();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (CrawlerPageInfoDTO crawlerPageInfoDTO : resultList) {
                if (null == crawlerPageInfoDTO) {
                    continue;
                }
                final String fromPageGroup = crawlerPageInfoDTO.getPageGroup();
                final CrawlerPageInfoDTO newDto = doHandler(crawlerPageInfoDTO);
                if (null != newDto) {
                    es.submit(new Runnable() {

                        @Override
                        public void run() {
                            LOGGER.info("to update crawlerPageInfoDTO:" + newDto.getId() + "from pageGroup:" + fromPageGroup + ", to pageGroup:" + newDto.getPageGroup());
                            boolean result = pageInfoService.update(newDto);
                            LOGGER.info("result:"+result);
                        }

                    });

                }
            }
        }
    }

    private static final String REGEX = "((http://www\\.techweb\\.com\\.cn/\\w+/\\d{4}-\\d{2}-\\d{2}/\\d+)_(\\d+).shtml)";

    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private CrawlerPageInfoDTO doHandler(CrawlerPageInfoDTO pageInfo) {
        String pageGroup = pageInfo.getPageGroup();
        Map<String, Object> extras = pageInfo.getExtras();
        boolean flag = false;
        // 整理老数据
        if (StringUtils.isBlank(pageGroup)) {
            if (MapUtils.isNotEmpty(extras) && extras.containsKey("url")) {
                pageGroup = (String) extras.get("url");
                pageInfo.setPageGroup(pageGroup);
                pageInfo.setPageGroupIndex(1);
                flag = true;
            }
        } else {
            if(pageInfo.getId()==72619){
                LOGGER.info("123");
            }
            if (!flag) {
                String url = (String) extras.get("url");
                Matcher urlMatcher = PATTERN.matcher(url);
                if (urlMatcher.matches()) {
                        String newPageGroupPrefix = urlMatcher.group(2);
                        String newPageGroup = newPageGroupPrefix + ".shtml";
                        if (!newPageGroup.equalsIgnoreCase(pageGroup)) {
                            pageInfo.setPageGroup(newPageGroup);
                            String indexFor = urlMatcher.group(urlMatcher.groupCount());
                            Integer index = Integer.parseInt(indexFor);
                            pageInfo.setPageGroupIndex(index);
                            flag = true;
                        }
                } else {
                    if (!url.equalsIgnoreCase(pageGroup)) {
                        pageInfo.setPageGroup(url);
                        pageInfo.setPageGroupIndex(1);
                        flag = true;
                    }
                }
            }
        }
        if (flag) {
            return pageInfo;
        } else {
            return null;
        }
    }
}
