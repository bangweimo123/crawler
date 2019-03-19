package com.dianping.merchant.robot.crawler.common.processor.converter;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerConverterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConverter;

@CrawlerConverterTag(name = "dateTimeConverter")
public class DateTimeCrawlerConverter implements ICrawlerConverter {

    @Override
    public Object converter(Object sourceData, Object params) {
        if (null == sourceData) {
            return null;
        }
        if (sourceData instanceof String) {
            if (StringUtils.isNotBlank((String) sourceData)) {
                Long data = Long.parseLong((String) sourceData);
                data = data * 1000;
                Date date = new Date(data);
                return date;
            }
        }
        return null;
    }

}
