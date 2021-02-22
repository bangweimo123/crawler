package cn.mbw.crawler.core.processor.plugins.proxy;

import cn.mbw.crawler.core.processor.iface.ICrawlerProxyBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * ClassName:LSProxyPool
 *
 * @author ch
 * @version Ver 1.0
 * @Function: TODO ADD FUNCTION
 * @Date 2014-2-14 下午01:10:04
 * @see
 */
public class LSProxyPool extends ProxyPool {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private BlockingQueue<LSProxy> proxyQueue = new DelayQueue<LSProxy>();
    private Map<String, LSProxy> allProxy = new ConcurrentHashMap<String, LSProxy>();

    private int reuseInterval = 1500;// ms
    private int reviveTime = 2 * 60 * 60 * 1000;// ms

    private boolean validateWhenInit = false;
    private boolean isEnable = false;
    private String proxyFile;
    private static LSProxyUtil lsProxyUtil = LSProxyUtil.init();

    public LSProxyPool(String proxyFile) {
        this.proxyFile = proxyFile;
        this.setValidateWhenInit(true);
        if (!isEmptyFile(this.proxyFile)) {
            this.readProxyList();
        }
        this.saveProxyList();
    }

    public LSProxyPool(List<String[]> httpLSProxyList, String proxyFile) {
        this.proxyFile = proxyFile;
        this.setValidateWhenInit(true);
        if (!isEmptyFile(this.proxyFile)) {
            this.readProxyList();
        }
        if (CollectionUtils.isNotEmpty(httpLSProxyList)) {
            addProxy(httpLSProxyList.toArray(new String[httpLSProxyList.size()][]));
            this.saveProxyList();
        }
    }

    public void setValidateWhenInit(boolean validateWhenInit) {
        this.validateWhenInit = validateWhenInit;
    }

    public String getproxyFile() {
        return proxyFile;
    }

    private boolean isEmptyFile(String path) {
        File file = new File(path);
        if (file.exists() && file.length() != 0) {
            return false;
        }
        return true;
    }

    private void saveProxyList() {
        if (allProxy.size() == 0) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getproxyFile());
            if (MapUtils.isNotEmpty(allProxy)) {
                List<String> hostPortPairs = new ArrayList<>();
                for (Map.Entry<String, LSProxy> entry : allProxy.entrySet()) {
                    LSProxy proxy = entry.getValue();
                    //将有效的回写入文件
                    if (lsProxyUtil.validateProxy2(proxy.getHttpHost())) {
                        String hostName = proxy.getHttpHost().getHostName();
                        int port = proxy.getHttpHost().getPort();
                        String hostPortPair = hostName + ":" + port;
                        hostPortPairs.add(hostPortPair);
                    }
                }
                IOUtils.writeLines(hostPortPairs, "\n", fos);
            }
        } catch (IOException e) {
            logger.error("exception for saveProxyList", e);
        } finally {
            if (null != fos) {
                IOUtils.closeQuietly(fos);
            }
        }
    }

    private Map<String, LSProxy> prepareForSaving() {
        Map<String, LSProxy> tmp = new HashMap<String, LSProxy>();
        for (Map.Entry<String, LSProxy> e : allProxy.entrySet()) {
            LSProxy p = e.getValue();
            p.setFailedNum(0);
            tmp.put(e.getKey(), p);
        }
        return tmp;
    }

    private void readProxyList() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getproxyFile());
            List<String> proxyList = IOUtils.readLines(fis);
            List<String[]> proxyPairList = new ArrayList<String[]>();
            if (CollectionUtils.isNotEmpty(proxyList)) {
                for (String lsProxyItem : proxyList) {
                    String[] LSProxyPair = StringUtils.split(lsProxyItem, ":");
                    proxyPairList.add(LSProxyPair);
                }
            }
            addProxy(proxyPairList.toArray(new String[proxyPairList.size()][]));

        } catch (Exception e) {
            logger.error("exception for saveProxyList", e);
        } finally {
            if (null != fis) {
                IOUtils.closeQuietly(fis);
            }
        }
    }

    private void addProxy(Map<String, LSProxy> httpLSProxyMap) {
        isEnable = true;
        for (Map.Entry<String, LSProxy> entry : httpLSProxyMap.entrySet()) {
            try {
                if (allProxy.containsKey(entry.getKey())) {
                    continue;
                }
                if (!validateWhenInit || lsProxyUtil.validateProxy2(entry.getValue().getHttpHost())) {
                    entry.getValue().setFailedNum(0);
                    entry.getValue().setReuseTimeInterval(reuseInterval);
                    proxyQueue.add(entry.getValue());
                    allProxy.put(entry.getKey(), entry.getValue());
                }
            } catch (NumberFormatException e) {
                logger.error("HttpHost init error:", e);
            }
        }
        logger.info("LSProxy pool size>>>>" + allProxy.size());
    }

    public void addProxy(String[]... httpLSProxyList) {
        isEnable = true;
        for (String[] s : httpLSProxyList) {
            try {
                if (allProxy.containsKey(s[0])) {
                    continue;
                }
                HttpHost item = new HttpHost(InetAddress.getByName(s[0]), Integer.valueOf(s[1]));
                if (!validateWhenInit || lsProxyUtil.validateProxy2(item)) {
                    LSProxy p = new LSProxy(item, reuseInterval);
                    proxyQueue.add(p);
                    allProxy.put(s[0], p);
                }
            } catch (NumberFormatException e) {
                logger.error("HttpHost init error:", e);
            } catch (UnknownHostException e) {
                logger.error("HttpHost init error:", e);
            }
        }
        logger.info("LSProxy pool size>>>>" + allProxy.size());
    }

    public boolean removeProxy(String ip) {
        if (StringUtils.isNotBlank(ip)) {
            LSProxy proxy = allProxy.remove(ip);
            if (null != proxy) {
                ICrawlerProxyBuilder crawlerProxyBuilder = CrawlerProxyFactory.getProxy(null);
                if (this.getProxyNum() < 10) {
                    List<String[]> proxyHosts = crawlerProxyBuilder.builder(10);
                    this.addProxy(proxyHosts.toArray(new String[proxyHosts.size()][]));
                    this.saveProxyList();
                }
                return true;
            }

        }
        return false;
    }

    public HttpHost getProxy() {
        LSProxy proxy = null;
        try {
            Long time = System.currentTimeMillis();
            proxy = proxyQueue.take();
            double costTime = (System.currentTimeMillis() - time) / 1000.0;
            if (costTime > reuseInterval) {
                logger.info("get proxy time >>>> " + costTime);
            }
            LSProxy p = allProxy.get(proxy.getHttpHost().getAddress().getHostAddress());
            p.setLastBorrowTime(System.currentTimeMillis());
            p.borrowNumIncrement(1);
        } catch (InterruptedException e) {
            logger.error("get proxy error", e);
        }
        if (proxy == null) {
            throw new NoSuchElementException();
        }
        return proxy.getHttpHost();
    }

    public void returnProxy(HttpHost host, int statusCode) {
        LSProxy p = allProxy.get(host.getAddress().getHostAddress());
        if (p == null) {
            return;
        }
        switch (statusCode) {
            case Proxy.SUCCESS:
                p.setReuseTimeInterval(reuseInterval);
                p.setFailedNum(0);
                p.setFailedErrorType(new ArrayList<Integer>());
                p.recordResponse();
                p.successNumIncrement(1);
                break;
            case Proxy.ERROR_403:
                // banned,try larger interval
                p.fail(Proxy.ERROR_403);
                p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
                logger.info(host + " >>>> reuseTimeInterval is >>>> " + p.getReuseTimeInterval() / 1000.0);
                break;
            case Proxy.ERROR_BANNED:
                p.fail(Proxy.ERROR_BANNED);
                p.setReuseTimeInterval(10 * 60 * 1000 * p.getFailedNum());
                logger.warn("this proxy is banned >>>> " + p.getHttpHost());
                logger.info(host + " >>>> reuseTimeInterval is >>>> " + p.getReuseTimeInterval() / 1000.0);
                break;
            case Proxy.ERROR_404:
                //p.fail(Proxy.ERROR_404);
                // p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
                break;
            default:
                p.fail(statusCode);
                break;
        }
        if (p.getFailedNum() > 20) {
            // allProxy.remove(host.getAddress().getHostAddress());
            p.setReuseTimeInterval(reviveTime);
            logger.error("remove proxy >>>> " + host + ">>>>" + p.getFailedType() + " >>>> remain proxy >>>> " + proxyQueue.size());
            return;
        }
        if (p.getFailedNum() % 5 == 0) {
            if (!lsProxyUtil.validateProxy2(host)) {
                // allProxy.remove(host.getAddress().getHostAddress());
                p.setReuseTimeInterval(reviveTime);
                logger.error("remove proxy >>>> " + host + ">>>>" + p.getFailedType() + " >>>> remain proxy >>>> " + proxyQueue.size());
                return;
            }
        }
        try {
            proxyQueue.put(p);
        } catch (InterruptedException e) {
            logger.warn("proxyQueue return proxy error", e);
        }
    }

    public String allProxyStatus() {
        String re = "all proxy info >>>> \n";
        for (Map.Entry<String, LSProxy> entry : allProxy.entrySet()) {
            re += entry.getValue().toString() + "\n";
        }
        return re;

    }

    public int getIdleNum() {
        return proxyQueue.size();
    }

    public int getProxyNum() {
        return allProxy.size();
    }

    public int getReuseInterval() {
        return reuseInterval;
    }

    public void setReuseInterval(int reuseInterval) {
        this.reuseInterval = reuseInterval;
    }

    public void enable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public boolean isEnable() {
        return isEnable;
    }
}

