package com.dianping.merchant.robot.crawler.common.processor.spider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.merchant.robot.crawler.common.processor.CommonPageProcessor;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConfigParser;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerResultHandler;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerUrlManager;
import com.dianping.merchant.robot.crawler.common.processor.iface.impl.JsonCrawlerConfigParser;
import com.dianping.merchant.robot.crawler.common.processor.plugins.downloader.CrawlerDownloaderFactory;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.CrawlerConfig;
import com.dianping.merchant.robot.crawler.common.processor.plugins.pipeline.AvatorLoggerPipeLine;
import com.dianping.merchant.robot.crawler.common.processor.plugins.pipeline.DpResultPipeLine;
import com.dianping.merchant.robot.crawler.common.processor.plugins.resulthandler.CrawlerResultHandlerFactory;
import com.dianping.merchant.robot.crawler.common.processor.plugins.scheduler.DpUrlManagerScheduler;
import com.dianping.merchant.robot.crawler.common.processor.plugins.urlmanager.CrawlerUrlManagerFactory;
import com.dianping.merchant.robot.crawler.common.processor.spider.trigger.CrawlerTriggerController;
import com.dianping.merchant.robot.crawler.common.processor.utils.ThreadPoolUtils;

/**
 * 爬虫构建工厂，能够根据一个文件初始化好所有的爬虫
 * 
 * @author mobangwei
 * 
 */
public class SpiderFactory {
    private static AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(SpiderFactory.class);

    private static Map<String, DpSpider> spiderCache = new HashMap<String, DpSpider>();

    private static DpSpider initSpider(String domainTag, ICrawlerConfigParser configParser) {
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
            Pipeline dpResultPipeLine = new DpResultPipeLine(urlManager, resultHandler);
            Assert.notNull(dpResultPipeLine, "dpResultPipeLine init error");

            LOGGER.info("parser basePipeLine success");
            // scheduler 公用调度器
            Scheduler scheduler = new DpUrlManagerScheduler(urlManager, config);
            Assert.notNull(scheduler, "scheduler init error");

            LOGGER.info("parser scheduler success");

            // spider 爬虫初始化,顺序不能替换
            DpSpider dpSpider = DpSpider

            .create(processor)

            .domainTag(domainTag)

            .thread(config.getCrawlerBaseInfo().getThreadCount())

            .setScheduler(scheduler)

            .setUUID(domainTag)

            .addUrl(config.getCrawlerBaseInfo().getBaseUrl())

            .addPipeline(new AvatorLoggerPipeLine())

            .addPipeline(dpResultPipeLine)

            .setExecutorService(ThreadPoolUtils.getExcutorService())
            ;

            // spider下载器注册
            if (MapUtils.isNotEmpty(config.getCrawlerBaseInfo().getDownloaderPlugins())) {
                for (String regex : config.getCrawlerBaseInfo().getDownloaderPlugins().keySet()) {
                    dpSpider.addDownloaderPlugin(regex, CrawlerDownloaderFactory.getDownloader(config.getCrawlerBaseInfo().getDownloaderPlugins().get(regex)));
                }
            }
            LOGGER.info("init dpSpider success");
            // spider监听器注册
            SpiderListener listener = new DpSpiderListener(scheduler, dpSpider, urlManager);
            dpSpider = dpSpider.addSpiderListner(listener);
            LOGGER.info("register dpSpiderListener success");

            // spider触发器注册
            CrawlerTriggerController.registerTrigger(domainTag, config.getCrawlerTrigger());
            LOGGER.info("initSpider success,domainTag=[" + domainTag + "]");
            return dpSpider;
        } catch (Exception e) {
            LOGGER.error("initSpider error:" + e.getMessage(), e);
            LOGGER.info("initSpider error,domainTag=[" + domainTag + "]");
        }
        return null;
    }

    public static void initSpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_INIT", domainTag);
        try {
            DpSpider dpSpider = initSpider(domainTag, new JsonCrawlerConfigParser());
            spiderCache.put(domainTag, dpSpider);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    /**
     * 停止spider并且删除缓存
     * 
     * @param domainTag
     */
    public static void destorySpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_DESTORY", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_STOP_EVENT", domainTag);
            spiderCache.get(domainTag).destorySpider();
            spiderCache.remove(domainTag);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }


    public static void stopSpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_STOP", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_STOP_EVENT", domainTag);
            spiderCache.get(domainTag).stopSpider();
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    public static void startSpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_START", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_START_EVENT", domainTag);
            spiderCache.get(domainTag).startSpider();
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

}
