package com.lifesense.kuafu.crawler.core.test;

import com.lifesense.kuafu.crawler.core.base.BaseTest;
import cn.mbw.crawler.core.processor.BooheeResultHandler;
import cn.mbw.crawler.core.processor.iface.ICrawlerConfigParser;
import cn.mbw.crawler.core.processor.iface.impl.JsonCrawlerConfigParser;
import cn.mbw.crawler.core.processor.plugins.downloader.LSHttpClientDownloader;
import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author bangwei.mo[bangwei.mo@lifesense.com]
 * @Date 2020-04-07 16:34
 * @Modify
 */
public class KuafuCrawlerBooheeTest extends BaseTest {

    @Test
    public void testBooheeWith() throws Exception {
        CSVFileUtil csvFileUtil = new CSVFileUtil("/Users/mobangwei/Desktop/boohee.csv");
        ICrawlerConfigParser crawlerConfigParser = new JsonCrawlerConfigParser();
        CrawlerConfig config = crawlerConfigParser.parser("boohee-unit");
        LSHttpClientDownloader downloader = new LSHttpClientDownloader();
        String[] headers = {"enName", "data"};
        downloader.setThread(30);
        while (true) {
            String line = csvFileUtil.readLine();
            ArrayList arrayList = csvFileUtil.fromCSVLinetoArray(line);
            String url = (String) arrayList.get(3);
            String realName = StringUtils.substringAfter(url, "http://i.boohee.com/shiwu_more/");
            if (StringUtils.isNotBlank(realName)) {
                String download_url = "https://food.boohee.com/fb/v2/foods/" + realName + "/detail?tenant=1&platform=app&token=2V9vtxZqxVD7EoMZxtnqUS16uPxaoqzk";
                Task task = config.getSite().parse().toTask();
                Page page = downloader.download(new Request(download_url), task);
                Map<String, Object> resultFields = new HashMap<>();
                resultFields.put("enName", realName);
                resultFields.put("data", page.getRawText());
                BooheeResultHandler.writeCSV("/Users/mobangwei/Desktop/boohee-detail.csv", resultFields, headers);
                System.out.println(page.getRawText());
            }
        }
    }
}
