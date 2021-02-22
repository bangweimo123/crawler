package cn.mbw.crawler.core.processor.spider;

import cn.mbw.crawler.core.processor.PageProcessorFactory;
import cn.mbw.crawler.core.processor.plugins.downloader.CrawlerDownloaderFactory;
import cn.mbw.crawler.core.processor.plugins.entity.CrawlerConfig;
import cn.mbw.crawler.core.processor.plugins.pipeline.LSResultPipeLine;
import cn.mbw.crawler.core.processor.plugins.pipeline.LoggerPipeLine;
import cn.mbw.crawler.core.processor.plugins.resulthandler.CrawlerResultHandlerFactory;
import cn.mbw.crawler.core.processor.plugins.scheduler.UrlManagerScheduler;
import cn.mbw.crawler.core.processor.plugins.urlmanager.CrawlerUrlManagerFactory;
import cn.mbw.crawler.core.processor.spider.trigger.CrawlerTriggerController;
import cn.mbw.crawler.core.processor.CommonPageProcessor;
import com.lifesense.kuafu.crawler.core.processor.*;
import cn.mbw.crawler.core.processor.iface.ICrawlerConfigParser;
import cn.mbw.crawler.core.processor.iface.ICrawlerResultHandler;
import cn.mbw.crawler.core.processor.iface.ICrawlerUrlManager;
import cn.mbw.crawler.core.processor.iface.impl.JsonCrawlerConfigParser;
import cn.mbw.crawler.core.processor.utils.ThreadPoolUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
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

    private static LSSpider _initSpider(String domainTag, ICrawlerConfigParser configParser) {
        LOGGER.info("initSpider starting...,domainTag=[" + domainTag + "]");
        Assert.notNull(configParser, "configParser can not be null");
        CrawlerConfig config = configParser.parser(domainTag);
        return _initSpider(config);
    }

    public static void initSpider(String domainTag) {
        try {
            LSSpider dpSpider = _initSpider(domainTag, new JsonCrawlerConfigParser());
            spiderCache.put(domainTag, dpSpider);
        } catch (Exception e) {
            LOGGER.error("initSpider error for exception", e);
        }
    }

    public static void initSpider(CrawlerConfig config) {
        try {
            LSSpider dpSpider = _initSpider(config);
            spiderCache.put(config.getDomainTag(), dpSpider);
        } catch (Exception e) {
            LOGGER.error("initSpider error for exception", e);
        }
    }

    private static LSSpider _initSpider(CrawlerConfig config) {
        LOGGER.info("initSpider starting...,domainTag=[" + config.getDomainTag() + "]");
        try {
            Assert.notNull(config, "crawlerConfig parser error");
            LOGGER.info("parser config success");
            // processor 定义公用执行器
            PageProcessor processor = null;
            if (StringUtils.isNotBlank(config.getPageProcessor())) {
                String pageProcessor = config.getPageProcessor();
                processor = PageProcessorFactory.getPageProcessor(pageProcessor);
            } else {
                processor = new CommonPageProcessor(config);
            }
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
            Pipeline pipeline = new LSResultPipeLine(urlManager, resultHandler);
            Assert.notNull(pipeline, "dpResultPipeLine init error");

            LOGGER.info("parser basePipeLine success");
            // scheduler 公用调度器
            Scheduler scheduler = new UrlManagerScheduler(urlManager, config);
            Assert.notNull(scheduler, "scheduler init error");

            LOGGER.info("parser scheduler success");

            // spider 爬虫初始化,顺序不能替换
            LSSpider lsSpider = LSSpider

                    .create(processor)

                    .domainTag(config.getDomainTag())

                    .thread(config.getCrawlerBaseInfo().getThreadCount())

                    .setScheduler(scheduler)

                    .setUUID(config.getDomainTag())

                    .addUrl(config.getCrawlerBaseInfo().getBaseUrl())

                    .addPipeline(new LoggerPipeLine())

                    .addPipeline(pipeline)

                    .setExecutorService(ThreadPoolUtils.getExcutorService());

            // spider下载器注册
            if (MapUtils.isNotEmpty(config.getCrawlerBaseInfo().getDownloaderPlugins())) {
                for (String regex : config.getCrawlerBaseInfo().getDownloaderPlugins().keySet()) {
                    lsSpider.addDownloaderPlugin(regex, CrawlerDownloaderFactory.getDownloader(config.getCrawlerBaseInfo().getDownloaderPlugins().get(regex)));
                }
            }
            LOGGER.info("init dpSpider success");
            // spider监听器注册
//            SpiderListener listener = new LSSpiderListener(scheduler, lsSpider, urlManager);
//            if (StringUtils.isNotBlank(config.getCrawlerBaseInfo().getListener())) {
//                String listenerName = config.getCrawlerBaseInfo().getListener();
//                switch (listenerName) {
//                    case "boohee":
//                        listener = new BooheeLSSpiderListener(config, scheduler, lsSpider, urlManager);
//                        break;
//                }
//            }
//            lsSpider = lsSpider.addSpiderListner(listener);
            LOGGER.info("register dpSpiderListener success");

            // spider触发器注册
            CrawlerTriggerController.registerTrigger(config.getDomainTag(), config.getCrawlerTrigger());
            LOGGER.info("initSpider success,domainTag=[" + config.getDomainTag() + "]");
            return lsSpider;
        } catch (Exception e) {
            LOGGER.error("initSpider error:" + e.getMessage(), e);
            LOGGER.info("initSpider error,domainTag=[" + config.getDomainTag() + "]");
        }
        return null;
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

    public static LSSpider getSpider(String domainTag) {
        return spiderCache.get(domainTag);
    }

}
