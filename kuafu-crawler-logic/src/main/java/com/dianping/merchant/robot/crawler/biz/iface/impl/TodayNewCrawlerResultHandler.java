package com.dianping.merchant.robot.crawler.biz.iface.impl;

import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerResultHandler;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TodayNewCrawlerResultHandler implements ICrawlerResultHandler {
    @Override
    public ProStatus handler(Map<String, Object> resultFields, Map<String, Object> contextParams) {
        return null;
    }
//    private static final Logger LOGGER = LoggerFactory.getLogger(TodayNewCrawlerResultHandler.class);
//
//    @Resource
//    private PageInfoExternalService todayNewPageInfoService;
//
//    public static class FieldConstants {
//        private static final String TITLE = "title";
//        private static final String CONTENT = "content";
//        private static final String SUMMARY = "summary";
//        private static final String SOURCE = "source";
//        private static final String AUTHOR = "author";
//        private static final String DATE = "date";
//        private static final String THUMBNAIL = "thumbnail";
//        private static final String CURRENTURL = "currentUrl";
//        private static final String KEYWORD = "keyword";
//        private static final String DOMAINTAG = "domainTag";
//    }
//
//    public static class PageGroupConstants {
//        private static final String PAGEGROUP = "pageGroup";
//        private static final String PAGEGROUPINDEX = "pageGroupIndex";
//    }
//
//    @Override
//    public ProStatus handler(Map<String, Object> resultFields, Map<String, Object> contextParams) {
//        try {
//            CrawlerPageInfoContext pageInfoContext = toContext(resultFields, contextParams);
//            reWriteForPageGroup(resultFields, contextParams, pageInfoContext);
//            boolean isAdd = todayNewPageInfoService.createPageInfo(pageInfoContext);
//            LOGGER.info("add pageInfoDTO result:" + isAdd);
//            Assert.isTrue(isAdd, "add to pageinfo error");
//            return ProStatus.success();
//        } catch (IllegalArgumentException iae) {
//            LOGGER.warn("error to parser result:" + iae.getMessage(), iae);
//            return ProStatus.fail(ProMessageCode.RESULT_PARSER_ERROR.getCode(), iae.getMessage());
//        } catch (Exception e) {
//            LOGGER.error("add result error");
//            return ProStatus.fail(ProMessageCode.RESULT_PARSER_ERROR.getCode(), e.getMessage());
//        }
//    }
//
//    protected CrawlerPageInfoContext toContext(Map<String, Object> resultFields, Map<String, Object> contextParams) {
//        String title = (String) resultFields.get(FieldConstants.TITLE);
//        Assert.hasText(title, "标题不能为空");
//        String content = (String) resultFields.get(FieldConstants.CONTENT);
//        String subTitle = (String) resultFields.get(FieldConstants.SUMMARY);
//        String source = (String) resultFields.get(FieldConstants.SOURCE);
//        Assert.hasText(source, "文章来源不能为空");
//        String author = (String) resultFields.get(FieldConstants.AUTHOR);
//        Date date = (Date) resultFields.get(FieldConstants.DATE);
//        String thumbnail = (String) resultFields.get(FieldConstants.THUMBNAIL);
//        String url = (String) contextParams.get(FieldConstants.CURRENTURL);
//        @SuppressWarnings("unchecked")
//        List<String> keyword = (List<String>) resultFields.get(FieldConstants.KEYWORD);
//        String domainTag = (String) contextParams.get(FieldConstants.DOMAINTAG);
//
//        CrawlerPageInfoDTO pageInfoDTO = new CrawlerPageInfoDTO();
//        pageInfoDTO.setMainTitle(title);
//        pageInfoDTO.setContent(content);
//        pageInfoDTO.setAddTime(new Date());
//        pageInfoDTO.setSource(source);
//        pageInfoDTO.setSourceDate(date);
//        pageInfoDTO.setStatus(0);
//        pageInfoDTO.setSubTitle(subTitle);
//        pageInfoDTO.setSummary(subTitle);
//        pageInfoDTO.setThumbnail(thumbnail);
//        pageInfoDTO.setUpdateTime(new Date());
//        Map<String, Object> extras = new HashMap<String, Object>();
//        extras.put("author", author);
//        extras.put("url", url);
//        extras.put("domainTag", domainTag);
//        pageInfoDTO.setExtras(extras);
//        CrawlerPageInfoContext pageInfoContext = new CrawlerPageInfoContext();
//        pageInfoContext.setPageInfoDTO(pageInfoDTO);
//        pageInfoContext.setKeywords(keyword);
//        return pageInfoContext;
//    }
//
//    protected void reWriteForPageGroup(Map<String, Object> resultFields, Map<String, Object> contextParams, CrawlerPageInfoContext context) {
//        String pageGroup = (String) contextParams.get(PageGroupConstants.PAGEGROUP);
//        if (StringUtils.isNotBlank(pageGroup)) {
//            context.getPageInfoDTO().setPageGroup(pageGroup);
//            Integer pageGroupIndex = (Integer) contextParams.get(PageGroupConstants.PAGEGROUPINDEX);
//            if (null != pageGroupIndex) {
//                context.getPageInfoDTO().setPageGroupIndex(pageGroupIndex);
//            }
//        }
//    }
}
