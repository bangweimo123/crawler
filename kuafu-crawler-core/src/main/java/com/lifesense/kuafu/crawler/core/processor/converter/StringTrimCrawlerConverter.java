package com.lifesense.kuafu.crawler.core.processor.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerConverterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConverter;

@CrawlerConverterTag(name = "stringTrimConverter")
public class StringTrimCrawlerConverter implements ICrawlerConverter {

    @Override
    public Object converter(Object sourceData, Object params) {
        if (null == sourceData) {
            return null;
        }
        String data = (String) sourceData;
        return replaceBlank(data);
    }

    private static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
