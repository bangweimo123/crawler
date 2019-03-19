package com.dianping.merchant.robot.crawler.common.processor.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.util.DateUtils;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerConverterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConverter;

@CrawlerConverterTag(name = "dateConverter")
public class DateCrawlerConverter implements ICrawlerConverter {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(DateCrawlerConverter.class);

    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd";

    @Override
    public Object converter(Object sourceData, Object params) {
        String dateFormat = DEFAULT_DATEFORMAT;
        if (null != params) {
            dateFormat = (String) params;
        }
        DateFormat dateFormater = new SimpleDateFormat(dateFormat);
        if (null != sourceData) {
            String dateStr = (String) sourceData;
            if (StringUtils.isNotBlank(dateStr)) {
                try {
                    Date date = DateUtils.parse(dateStr, dateFormater);
                    return date;
                } catch (Exception e) {
                    LOGGER.error("parse date error", e);
                } finally {
                    LOGGER.info("to parse dateStr:" + dateStr);
                }
            }
        }
        return null;
    }
}
