package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.converter.ConverterFactory;
import cn.mbw.crawler.core.processor.iface.ICrawlerConverter;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import com.alibaba.fastjson.JSON;
import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import cn.mbw.crawler.core.processor.iface.ICrawlerResultHandler;
import com.lifesense.soa.contentmanagement.api.CrawlerDataService;
import com.lifesense.soa.contentmanagement.dto.DraftData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class MyResultHandler implements ICrawlerResultHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyResultHandler.class);
//    @Resource
//    private CrawlerDataService crawlerDataService;

    @Override
    public ProStatus handler(Map<String, Object> resultFields, Map<String, Object> contextParams) {
        DraftData draftData = new DraftData();
        if (resultFields.containsKey("content") && null != resultFields.get("content")) {
            String content = (String) resultFields.get("content");
            draftData.setDetail(content);
            if (resultFields.containsKey("source")) {
                String source = (String) resultFields.get("source");
                draftData.setAuthor(source);
            }
            if (resultFields.containsKey("title")) {
                draftData.setTitle((String) resultFields.get("title"));
            }
            if (resultFields.containsKey("keywords")) {
                String keywords = (String) resultFields.get("keywords");
                ICrawlerConverter stringSplitConverter = ConverterFactory.getConverter("stringSplitConverter");
                draftData.setLabels((List<String>) stringSplitConverter.converter(null, keywords, null));
            }
            if (resultFields.containsKey("addTime")) {
                ICrawlerConverter dateConverter = ConverterFactory.getConverter("dateConverter");
                String addTime = (String) resultFields.get("addTime");
                draftData.setAddTime((Date) dateConverter.converter(null, addTime, "yyyy-MM-dd HH:mm"));
            }
            if (contextParams.containsKey(CrawlerCommonConstants.ProcessorContextConstant.CUSTOM_PARAMS)) {
                Map<String, Object> customParams = (Map<String, Object>) contextParams.get(CrawlerCommonConstants.ProcessorContextConstant.CUSTOM_PARAMS);
                if (null != customParams && customParams.containsKey("siteId")) {
                    draftData.setSiteId((Integer) customParams.get("siteId"));
                }
            }
            if (resultFields.containsKey("imageUrl")) {
                draftData.setHeadImage((String) resultFields.get("imageUrl"));
            }
            draftData.setOriginUrl((String) contextParams.get("currentUrl"));
//            Boolean saveResult = crawlerDataService.saveCrawlerDraftData(draftData);
//            if (saveResult) {
//                contentToTxt("/data/webmagic/tmp/result.txt", JSON.toJSONString(draftData));
//            }
        } else {
            contentToTxt("/data/webmagic/tmp/noContent.txt", JSON.toJSONString(resultFields));
        }
        return ProStatus.success();
    }

    public static void contentToTxt(String filePath, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true));
            writer.write("\n" + content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
