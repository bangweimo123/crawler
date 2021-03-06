package cn.mbw.crawler.core.processor.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.mbw.crawler.core.processor.annotation.CrawlerConverterTag;
import cn.mbw.crawler.core.processor.iface.ICrawlerConverter;
import us.codecraft.webmagic.Page;

@CrawlerConverterTag(name = "stringTrimConverter")
public class StringTrimCrawlerConverter implements ICrawlerConverter {

    @Override
    public Object converter(Page page, Object sourceData, Object params) {
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
