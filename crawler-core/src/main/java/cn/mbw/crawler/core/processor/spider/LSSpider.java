package cn.mbw.crawler.core.processor.spider;

import cn.mbw.crawler.core.processor.plugins.downloader.LSHttpClientDownloader;
import cn.mbw.crawler.core.processor.plugins.entity.ProMessageCode;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import cn.mbw.crawler.core.processor.utils.RedisCountUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.selector.thread.CountableThreadPool;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对spider的重写，添加一些新功能
 *
 * @author mobangwei
 */
public class LSSpider extends Spider {
    private static final Logger LOGGER = LoggerFactory.getLogger(LSSpider.class);
    protected String domainTag;

    protected String baseUrl;

    private Date startTime;
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

    public LSSpider setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public Map<String, Downloader> getDownloaderPlugin() {
        return downloaderPlugin;
    }

    public void setDownloaderPlugin(Map<String, Downloader> downloaderPlugin) {
        this.downloaderPlugin = downloaderPlugin;
    }

    public LSSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static LSSpider create(PageProcessor pageProcessor) {
        return new LSSpider(pageProcessor);
    }

    public LSSpider destroyWhenExit(Boolean status) {
        this.destroyWhenExit = status;
        return this;
    }

    public LSSpider domainTag(String domainTag) {
        this.domainTag = domainTag;
        return this;
    }

    /**
     * 注册一个baseurl
     *
     * @param baseUrl
     * @return
     */
    public LSSpider registerBaseUrl(String baseUrl) {
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
    public LSSpider startUrls(List<String> startUrls) {
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
    public LSSpider startRequest(List<Request> startRequests) {
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
    public LSSpider setUUID(String uuid) {
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
    public LSSpider scheduler(Scheduler scheduler) {
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
    public LSSpider setScheduler(Scheduler scheduler) {
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
    public LSSpider pipeline(Pipeline pipeline) {
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
    public LSSpider addPipeline(Pipeline pipeline) {
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
    public LSSpider setPipelines(List<Pipeline> pipelines) {
        super.setPipelines(pipelines);
        return this;
    }

    /**
     * clear the pipelines set
     *
     * @return this
     */
    public LSSpider clearPipeline() {
        super.clearPipeline();
        return this;
    }

    public LSSpider startUrls(String... startUrls) {
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
    public LSSpider downloader(Downloader downloader) {
        return setDownloader(downloader);
    }

    public LSSpider addDownloaderPlugin(String urlRegex, Downloader downloader) {
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
    public LSSpider setDownloader(Downloader downloader) {
        super.setDownloader(downloader);
        return this;
    }

    /**
     * Add urls to crawl. <br/>
     *
     * @param urls
     * @return
     */
    public LSSpider addUrl(String... urls) {
        super.addUrl(urls);
        return this;
    }

    /**
     * Add urls with information to crawl.<br/>
     *
     * @param requests
     * @return
     */
    public LSSpider addRequest(Request... requests) {
        super.addRequest(requests);
        return this;
    }

    /**
     * start with more than one threads
     *
     * @param threadNum
     * @return this
     */
    public LSSpider thread(int threadNum) {
        super.thread(threadNum);
        return this;
    }

    /**
     * start with more than one threads
     *
     * @param threadNum
     * @return this
     */
    public LSSpider thread(ExecutorService executorService, int threadNum) {
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
    public LSSpider setExitWhenComplete(boolean exitWhenComplete) {
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
    public LSSpider setSpawnUrl(boolean spawnUrl) {
        super.setSpawnUrl(spawnUrl);
        return this;
    }

    public LSSpider setExecutorService(ExecutorService executorService) {
        super.setExecutorService(executorService);
        return this;
    }

    public LSSpider setSpiderListeners(List<SpiderListener> spiderListeners) {
        super.setSpiderListeners(spiderListeners);
        return this;
    }

    public LSSpider addSpiderListner(SpiderListener... spiderListeners) {
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
        RedisCountUtils.reset(domainTag);
        this.start();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.setName("LSSpider-Async-Thread-" + this.getDomainTag());
        thread.setDaemon(false);
        thread.start();
    }

    public void stopSpider() {
        if (spiderIsRun()) {
            this.stop();
            LOGGER.info("spider is stoped,domainTag:" + domainTag);
        }
    }

    public void close() {
        destroyEach(downloader);
        destroyEach(pageProcessor);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
//        threadPool.shutdown();
    }

    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroySpider() {
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

    protected void initComponent() {
        if (downloader == null) {
            this.downloader = new LSHttpClientDownloader();
        }
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
        }
        downloader.setThread(threadNum);
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
        if (startRequests != null) {
            for (Request request : startRequests) {
                scheduler.push(request, this);
            }
            startRequests.clear();
        }
        startTime = new Date();
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

    public void extractAndAddRequests(Page page, boolean spawnUrl) {
        if (spawnUrl && CollectionUtils.isNotEmpty(page.getTargetRequests())) {
            for (Request request : page.getTargetRequests()) {
                addRequest(request);
            }
        }
    }

    public Date getStartTime() {
        return startTime;
    }
}
