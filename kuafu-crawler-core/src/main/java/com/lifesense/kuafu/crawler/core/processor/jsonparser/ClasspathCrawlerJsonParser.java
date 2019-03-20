package com.lifesense.kuafu.crawler.core.processor.jsonparser;

import com.lifesense.kuafu.crawler.core.processor.utils.DomainTagUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

@Component
public class ClasspathCrawlerJsonParser implements ICrawlerJsonParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathCrawlerJsonParser.class);

    @Override
    public void init() {
        register("classpath*:json/*.json");
    }


    private static String readFile(URL url) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            InputStream is = url.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.defaultCharset());
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error("read json File error");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("close read json File error");
                }
            }
        }
        return laststr;
    }


    public static void register(String filePath) {
        try {
            ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = patternResolver.getResources(filePath);
            if (null != resources && resources.length > 0) {
                for (Resource resource : resources) {
                    String fileName = resource.getFilename();
                    String domainTag = StringUtils.substringBefore(fileName, ".json");
                    URL url = resource.getURL();
                    String jsonData = readFile(url);
                    DomainTagUtils.register(domainTag, jsonData);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("parse json file error:" + filePath, e);
        }
    }
}
