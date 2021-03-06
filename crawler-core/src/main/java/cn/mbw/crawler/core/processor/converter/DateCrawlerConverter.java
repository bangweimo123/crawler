package cn.mbw.crawler.core.processor.converter;

import java.util.Date;

import cn.mbw.crawler.core.processor.annotation.CrawlerConverterTag;
import cn.mbw.crawler.core.processor.iface.ICrawlerConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;

@CrawlerConverterTag(name = "dateConverter")
public class DateCrawlerConverter implements ICrawlerConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateCrawlerConverter.class);

    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd";

    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        String dateFormat = DEFAULT_DATEFORMAT;
        if (null != params) {
            dateFormat = (String) params;
        }
        if (null != sourceData) {
            String dateStr = (String) sourceData;
            if (StringUtils.isNotBlank(dateStr)) {
                try {
                    Date date = DateUtils.parseDate(dateStr, dateFormat);
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
