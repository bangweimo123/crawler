package com.lifesense.kuafu.crawler.core.processor.spider;

import com.lifesense.kuafu.crawler.core.processor.CommonPageProcessor;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConfigParser;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerResultHandler;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerUrlManager;
import com.lifesense.kuafu.crawler.core.processor.iface.impl.JsonCrawlerConfigParser;
import com.lifesense.kuafu.crawler.core.processor.plugins.downloader.CrawlerDownloaderFactory;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.CrawlerConfig;
import com.lifesense.kuafu.crawler.core.processor.plugins.pipeline.AvatorLoggerPipeLine;
import com.lifesense.kuafu.crawler.core.processor.plugins.pipeline.LSResultPipeLine;
import com.lifesense.kuafu.crawler.core.processor.plugins.resulthandler.CrawlerResultHandlerFactory;
import com.lifesense.kuafu.crawler.core.processor.plugins.scheduler.DpUrlManagerScheduler;
import com.lifesense.kuafu.crawler.core.processor.plugins.urlmanager.CrawlerUrlManagerFactory;
import com.lifesense.kuafu.crawler.core.processor.spider.trigger.CrawlerTriggerController;
import com.lifesense.kuafu.crawler.core.processor.utils.ThreadPoolUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫构建工厂，能够根据一个文件初始化好所有的爬虫
 *
 * @author mobangwei
 */
public class SpiderFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(SpiderFactory.class);

    private static Map<String, LSSpider> spiderCache = new HashMap<String, LSSpider>();

    private static LSSpider initSpider(String domainTag, ICrawlerConfigParser configParser) {
        LOGGER.info("initSpider starting...,domainTag=[" + domainTag + "]");
        try {
            Assert.notNull(configParser, "configParser can not be null");
            CrawlerConfig config = configParser.parser(domainTag);
            Assert.notNull(config, "crawlerConfig parser error");
            LOGGER.info("parser config success");
            // processor 定义公用执行器
            CommonPageProcessor processor = new CommonPageProcessor(config);
            Assert.notNull(processor, "processor init error");
            LOGGER.info("parser processor success");
            // urlManager 获取URL管理器
            ICrawlerUrlManager urlManager = CrawlerUrlManagerFactory.getUrlManager(config.getCrawlerBaseInfo().getUrlManager());
            Assert.notNull(urlManager, "urlManager init error");
            LOGGER.info("parser urlManager success");

            // resultHandler 获取结果处理器
            ICrawlerResultHandler resultHandler = CrawlerResultHandlerFactory.getResultHandler(config.getCrawlerBaseInfo().getResultHandler());
            Assert.notNull(resultHandler, "resultHandler init error");
            LOGGER.info("parser resultHandler success");
            // pipeLine 结果PipeLine定义
            Pipeline dpResultPipeLine = new LSResultPipeLine(urlManager, resultHandler);
            Assert.notNull(dpResultPipeLine, "dpResultPipeLine init error");

            LOGGER.info("parser basePipeLine success");
            // scheduler 公用调度器
            Scheduler scheduler = new DpUrlManagerScheduler(urlManager, config);
            Assert.notNull(scheduler, "scheduler init error");

            LOGGER.info("parser scheduler success");

            // spider 爬虫初始化,顺序不能替换
            LSSpider lsSpider = LSSpider

                    .create(processor)

                    .domainTag(domainTag)

                    .thread(config.getCrawlerBaseInfo().getThreadCount())

                    .setScheduler(scheduler)

                    .setUUID(domainTag)

                    .addUrl(config.getCrawlerBaseInfo().getBaseUrl())

                    .addPipeline(new AvatorLoggerPipeLine())

                    .addPipeline(dpResultPipeLine)

                    .setExecutorService(ThreadPoolUtils.getExcutorService());

            // spider下载器注册
            if (MapUtils.isNotEmpty(config.getCrawlerBaseInfo().getDownloaderPlugins())) {
                for (String regex : config.getCrawlerBaseInfo().getDownloaderPlugins().keySet()) {
                    lsSpider.addDownloaderPlugin(regex, CrawlerDownloaderFactory.getDownloader(config.getCrawlerBaseInfo().getDownloaderPlugins().get(regex)));
                }
            }
            LOGGER.info("init dpSpider success");
            // spider监听器注册
            SpiderListener listener = new LSSpiderListener(scheduler, lsSpider, urlManager);
            lsSpider = lsSpider.addSpiderListner(listener);
            LOGGER.info("register dpSpiderListener success");

            // spider触发器注册
            CrawlerTriggerController.registerTrigger(domainTag, config.getCrawlerTrigger());
            LOGGER.info("initSpider success,domainTag=[" + domainTag + "]");
            return lsSpider;
        } catch (Exception e) {
            LOGGER.error("initSpider error:" + e.getMessage(), e);
            LOGGER.info("initSpider error,domainTag=[" + domainTag + "]");
        }
        return null;
    }

    public static void initSpider(String domainTag) {
        try {
            LSSpider dpSpider = initSpider(domainTag, new JsonCrawlerConfigParser());
            spiderCache.put(domainTag, dpSpider);
        } catch (Exception e) {
            LOGGER.error("initSpider error for exception", e);
        }
    }

    /**
     * 停止spider并且删除缓存
     *
     * @param domainTag
     */
    public static void destroySpider(String domainTag) {
        try {
            spiderCache.get(domainTag).destroySpider();
            spiderCache.remove(domainTag);
        } catch (Exception e) {
            LOGGER.error("exception for destroySpider", e);
        }
    }


    public static void stopSpider(String domainTag) {
        try {
            spiderCache.get(domainTag).stopSpider();
        } catch (Exception e) {
            LOGGER.error("exception for stopSpider", e);
        }
    }

    public static void startSpider(String domainTag) {
        try {
            spiderCache.get(domainTag).startSpider();
        } catch (Exception e) {
            LOGGER.error("exception for startSpider", e);
        }
    }

}
