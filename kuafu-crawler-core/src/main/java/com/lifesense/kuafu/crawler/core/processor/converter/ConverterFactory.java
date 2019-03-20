package com.lifesense.kuafu.crawler.core.processor.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerConverterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConverter;
import com.lifesense.kuafu.crawler.core.processor.spring.SpringLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * converterFactory
 *
 * @author mobangwei
 */
public class ConverterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterFactory.class);
    public Map<String, ICrawlerConverter> converterMap = new HashMap<String, ICrawlerConverter>();

    public void init() {
        try {
            Map<String, ICrawlerConverter> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerConverter.class);
            for (String beanName : result.keySet()) {
                ICrawlerConverter converter = result.get(beanName);
                CrawlerConverterTag converterTag = converter.getClass().getAnnotation(CrawlerConverterTag.class);
                if (null != converterTag) {
                    String name = converterTag.name();
                    converterMap.put(name, converter);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public ICrawlerConverter get(String coverterName) {
        return converterMap.get(coverterName);
    }

    private static class SingleHolder {
        private static ConverterFactory factory = new ConverterFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);

        public static ConverterFactory getInstance() {
            if (!isInit.get()) {
                factory.init();
                isInit.set(true);
            }
            return factory;
        }
    }

    public static ICrawlerConverter getConverter(String converterName) {
        ConverterFactory factory = SingleHolder.getInstance();
        return factory.get(converterName);
    }
}
