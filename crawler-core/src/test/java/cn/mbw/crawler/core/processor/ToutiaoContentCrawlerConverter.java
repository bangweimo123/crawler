package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.annotation.CrawlerConverterTag;
import cn.mbw.crawler.core.processor.iface.ICrawlerConverter;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrawlerConverterTag(name = "toutiaoContentConverter")
public class ToutiaoContentCrawlerConverter implements ICrawlerConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToutiaoContentCrawlerConverter.class);


    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        AtomicReference<String> result = new AtomicReference<>();
        Elements scripts = page.getHtml().getDocument().getElementsByTag("script");
        scripts.forEach(script -> {
            String regex = "articleInfo: \\{\\s*[\\n\\r]*\\s*title: '.*',\\s*[\\n\\r]*\\s*content: '(.*)',";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(script.toString());
            if (matcher.find()) {
                String content = matcher.group(1)
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&quot;", "\"")
                        .replace("&#x3D;", "=");
                LOGGER.info("content: {}", content);
                result.set(content);
            }
        });
        return result.get();
    }
}
