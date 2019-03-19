package com.dianping.merchant.robot.crawler.common.processor.converter;

import java.util.Arrays;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerConverterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConverter;

@CrawlerConverterTag(name = "stringSplitConverter")
public class StringSpiltCrawlerConverter implements ICrawlerConverter {

    private static final String DEFAULT_SPLIT = ",";

    @Override
    public Object converter(Object sourceData, Object params) {
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
