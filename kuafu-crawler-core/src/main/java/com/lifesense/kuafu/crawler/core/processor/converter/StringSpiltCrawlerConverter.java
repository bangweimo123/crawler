package com.lifesense.kuafu.crawler.core.processor.converter;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerConverterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConverter;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;

import java.util.Arrays;

@CrawlerConverterTag(name = "stringSplitConverter")
public class StringSpiltCrawlerConverter implements ICrawlerConverter {

    private static final String DEFAULT_SPLIT = ",";

    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        String splitStr = null;
        if (null == params) {
            splitStr = DEFAULT_SPLIT;
        } else {
            splitStr = (String) params;
        }
        if (null != sourceData) {
            String sourceDataStr = (String) sourceData;
            if (StringUtils.isNotBlank(sourceDataStr)) {
                String[] resultStrs = StringUtils.split(sourceDataStr, splitStr);
                if (null != resultStrs) {
                    return Arrays.asList(resultStrs);
                }
            }
        }
        return null;
    }

}
