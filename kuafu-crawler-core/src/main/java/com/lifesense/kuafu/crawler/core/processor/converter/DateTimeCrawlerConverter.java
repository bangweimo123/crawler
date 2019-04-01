package com.lifesense.kuafu.crawler.core.processor.converter;

import java.util.Date;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerConverterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConverter;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;

@CrawlerConverterTag(name = "dateTimeConverter")
public class DateTimeCrawlerConverter implements ICrawlerConverter {

    @Override
    public Object converter(Page page, Object sourceData, Object params) {
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
