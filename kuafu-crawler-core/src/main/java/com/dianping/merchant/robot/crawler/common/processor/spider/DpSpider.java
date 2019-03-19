package com.dianping.merchant.robot.crawler.common.processor.spider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.UrlUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.dianping.merchant.robot.crawler.common.processor.utils.MongoCounterUtils;
import com.dianping.pigeon.util.CollectionUtils;

/**
 * 对spider的重写，添加一些新功能
 * 
 * @author mobangwei
 * 
 */
public class DpSpider extends Spider {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(DpSpider.class);
    protected String domainTag;

    protected String baseUrl;
    /**
     * 用以存储针对特定的URL正则去匹配特定的downloader
     */
    protected Map<String, Downloader> downloaderPlugin = new ConcurrentHashMap<String, Downloader>();

    public String getDomainTag() {
        return domainTag;
    }

    public void setDomainTag(String domainTag) {
        this.domainTag = domainTag;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, Downloader> getDownloaderPlugin() {
        return downloaderPlugin;
    }

    public void setDownloaderPlugin(Map<String, Downloader> downloaderPlugin) {
        this.downloaderPlugin = downloaderPlugin;
    }

    public DpSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
        // TODO Auto-generated constructor stub
    }

    /**
     * create a spider with pageProcessor.
     * 
     * @param pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static DpSpider create(PageProcessor pageProcessor) {
        return new DpSpider(pageProcessor);
    }

    public DpSpider destroyWhenExit(Boolean status) {
        this.destroyWhenExit = status;
        return this;
    }

    public DpSpider domainTag(String domainTag) {
        this.domainTag = domainTag;
        return this;
    }

    /**
     * 注册一个baseurl
     * 
     * @param baseUrl
     * @return
     */
    public DpSpider registerBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this.addUrl(baseUrl);
    }

    /**
     * Set startUrls of DpSpider.<br>
     * Prior to startUrls of Site.
     * 
     * @param startUrls
     * @return this
     */
    public DpSpider startUrls(List<String> startUrls) {
        super.startUrls(startUrls);
        return this;
    }

    /**
     * Set startUrls of DpSpider.<br>
     * Prior to startUrls of Site.
     * 
     * @param startRequests
     * @return this
     */
    public DpSpider startRequest(List<Request> startRequests) {
        super.startRequest(startRequests);
        return this;
    }

    /**
     * Set an uuid for spider.<br>
     * Default uuid is domain of site.<br>
     * 
     * @param uuid
     * @return this
     */
    public DpSpider setUUID(String uuid) {
        super.setUUID(uuid);
        return this;
    }

    /**
     * set scheduler for DpSpider
     * 
     * @param scheduler
     * @return this
     * @Deprecated
     * @see #setScheduler(us.codecraft.webmagic.scheduler.Scheduler)
     */
    public DpSpider scheduler(Scheduler scheduler) {
        return setScheduler(scheduler);
    }

    /**
     * set scheduler for DpSpider
     * 
     * @param scheduler
     * @return this
     * @see Scheduler
     * @since 0.2.1
     */
    public DpSpider setScheduler(Scheduler scheduler) {
        super.setScheduler(scheduler);
        return this;
    }

    /**
     * add a pipeline for DpSpider
     * 
     * @param pipeline
     * @return this
     * @see #addPipeline(us.codecraft.webmagic.pipeline.Pipeline)
     * @deprecated
     */
    public DpSpider pipeline(Pipeline pipeline) {
        return addPipeline(pipeline);
    }

    /**
     * add a pipeline for DpSpider
     * 
     * @param pipeline
     * @return this
     * @see Pipeline
     * @since 0.2.1
     */
    public DpSpider addPipeline(Pipeline pipeline) {
        super.addPipeline(pipeline);
        return this;
    }

    /**
     * set pipelines for DpSpider
     * 
     * @param pipelines
     * @return this
     * @see Pipeline
     * @since 0.4.1
     */
    public DpSpider setPipelines(List<Pipeline> pipelines) {
        super.setPipelines(pipelines);
        return this;
    }

    /**
     * clear the pipelines set
     * 
     * @return this
     */
    public DpSpider clearPipeline() {
        super.clearPipeline();
        return this;
    }

    public DpSpider startUrls(String... startUrls) {
        checkIfRunning();
        this.startRequests = UrlUtils.convertToRequests(Arrays.asList(startUrls));
        return this;
    }

    /**
     * set the downloader of spider
     * 
     * @param downloader
     * @return this
     * @see #setDownloader(us.codecraft.webmagic.downloader.Downloader)
     * @deprecated
     */
    public DpSpider downloader(Downloader downloader) {
        return setDownloader(downloader);
    }

    public DpSpider addDownloaderPlugin(String urlRegex, Downloader downloader) {
        downloaderPlugin.put(urlRegex, downloader);
        return this;
    }

    /**
     * set the downloader of spider
     * 
     * @param downloader
     * @return this
     * @see Downloader
     */
    public DpSpider setDownloader(Downloader downloader) {
        super.setDownloader(downloader);
        return this;
    }

    /**
     * Add urls to crawl. <br/>
     * 
     * @param urls
     * @return
     */
    public DpSpider addUrl(String... urls) {
        super.addUrl(urls);
        return this;
    }

    /**
     * Add urls with information to crawl.<br/>
     * 
     * @param requests
     * @return
     */
    public DpSpider addRequest(Request... requests) {
        super.addRequest(requests);
        return this;
    }

    /**
     * start with more than one threads
     * 
     * @param threadNum
     * @return this
     */
    public DpSpider thread(int threadNum) {
        super.thread(threadNum);
        return this;
    }

    /**
     * start with more than one threads
     * 
     * @param threadNum
     * @return this
     */
    public DpSpider thread(ExecutorService executorService, int threadNum) {
        super.thread(executorService, threadNum);
        return this;
    }

    /**
     * Exit when complete. <br/>
     * True: exit when all url of the site is downloaded. <br/>
     * False: not exit until call stop() manually.<br/>
     * 
     * @param exitWhenComplete
     * @return
     */
    public DpSpider setExitWhenComplete(boolean exitWhenComplete) {
        super.setExitWhenComplete(exitWhenComplete);
        return this;
    }

    /**
     * Whether add urls extracted to download.<br>
     * Add urls to download when it is true, and just download seed urls when it is false. <br>
     * DO NOT set it unless you know what it means!
     * 
     * @param spawnUrl
     * @return
     * @since 0.4.0
     */
    public DpSpider setSpawnUrl(boolean spawnUrl) {
        super.setSpawnUrl(spawnUrl);
        return this;
    }

    public DpSpider setExecutorService(ExecutorService executorService) {
        super.setExecutorService(executorService);
        return this;
    }

    public DpSpider setSpiderListeners(List<SpiderListener> spiderListeners) {
        super.setSpiderListeners(spiderListeners);
        return this;
    }

    public DpSpider addSpiderListner(SpiderListener... spiderListeners) {
        List<SpiderListener> listenerList = null;
        if (CollectionUtils.isEmpty(super.getSpiderListeners())) {
            listenerList = new ArrayList<SpiderListener>();
        } else {
            listenerList = super.getSpiderListeners();
        }
        for (SpiderListener listener : spiderListeners) {
            if (!listenerList.contains(listener)) {
                listenerList.add(listener);
            }
        }
        super.setSpiderListeners(listenerList);
        return this;
    }

    public void startSpider() {
        // 在每次启动spider的时候默认重置计数器
        MongoCounterUtils.reset(domainTag);
        this.start();
    }

    public void stopSpider() {
        if (spiderIsRun()) {
            this.stop();
            LOGGER.info("spider is stoped,domainTag:" + domainTag);
        }
    }

    public void destorySpider() {
        stopSpider();
        this.close();
        LOGGER.info("spider is destory,domainTag:" + domainTag);
    }

    /**
     * 判断spider是否在运行
     * 
     * @return
     */
    public boolean spiderIsRun() {
        return stat.get() == STAT_RUNNING;
    }

    // 方法重写
    protected void processRequest(Request request) {
        // 添加下载器的正则获取
        Downloader realDownloader = downloader;
        if (MapUtils.isNotEmpty(downloaderPlugin)) {
            for (String urlRegex : downloaderPlugin.keySet()) {
                Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(request.getUrl());
                if (matcher.matches()) {
                    realDownloader = downloaderPlugin.get(urlRegex);
                }
            }
        }
        Page page = realDownloader.download(request, this);
        if (page == null) {
            sleep(site.getSleepTime());
            // 如果为空，则默认是成功状态
            request.putExtra("proStatus", ProStatus.fail(ProMessageCode.DOWNLOAD_ERROR.getCode()));
            onError(request);
            return;
        }
        // for cycle retry
        if (page.isNeedCycleRetry()) {
            extractAndAddRequests(page, true);
            sleep(site.getSleepTime());
            return;
        }
        pageProcessor.process(page);
        extractAndAddRequests(page, spawnUrl);
        if (!page.getResultItems().isSkip()) {
            for (Pipeline pipeline : pipelines) {
                pipeline.process(page.getResultItems(), this);
            }
        }
        sleep(site.getSleepTime());
    }
}
