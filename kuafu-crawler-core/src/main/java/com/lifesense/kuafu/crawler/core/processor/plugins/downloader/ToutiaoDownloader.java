package com.lifesense.kuafu.crawler.core.processor.plugins.downloader;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.collect.Sets;
import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerDownloaderTag;
import com.lifesense.kuafu.crawler.core.processor.plugins.proxy.LSProxyPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.client.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.HttpConstant;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrawlerDownloaderTag(name = "toutiaoDownloader")
public class ToutiaoDownloader extends HttpClientDownloader implements IDownloadCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToutiaoDownloader.class);
    private String AJAX_URL_TMP = "https://www.toutiao.com/pgc/ma/?page_type=1&max_behot_time=%s&uid=%s&media_id=%s&output=json&is_json=1&count=20&from=user_profile_app&version=2&as=%s&cp=%s&callback=jsonp6";
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();


    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Page download(Request request, Task task) {
        request.putExtra("isJSONPage", true);
        String toutiaoUrl = request.getUrl();
        preHandlerRequest(request);
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        LOGGER.info("downloading page {}", request.getUrl());
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpResponse = getHttpClient(site).execute(httpUriRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, toutiaoUrl, charset, httpResponse, task);
                onSuccess(request, task);
                return page;
            } else {
                LOGGER.warn("code error " + statusCode + "\t" + request.getUrl());
                return null;
            }
        } catch (HttpHostConnectException hce) {
            request.setUrl(toutiaoUrl);
            onFailed(request, task);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
        } catch (IOException e) {
            request.setUrl(toutiaoUrl);
            onFailed(request, task);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                LOGGER.warn("close response fail", e);
            }
        }
        return null;
    }

    protected Page handleResponse(Request request, String toutiaoUrl, String charset, HttpResponse httpResponse, Task task) throws IOException {
        String content = getContent(charset, httpResponse);
        if (content.contains("jsonp")) {
            content = StringUtils.substring(content, 7, content.length() - 1);
            Page page = new Page();
            page.setRawText(content);
            page.setUrl(new PlainText(toutiaoUrl));
            request.setUrl(toutiaoUrl);
            page.setRequest(request);
            page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            return page;
        }
        return null;

    }

    protected void preHandlerRequest(Request request) {
        String url = request.getUrl();
        String userId = null;
        String mid = null;
        Pattern pattern = Pattern.compile("toutiao-user:(\\d+)\\|toutiao-mid:(\\d+)\\|serialNumber:(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            userId = matcher.group(1);
            mid = matcher.group(2);
        }
        Long maxBehotTime = 0l;
        if (request.getExtra("max_behot_time") != null) {
            maxBehotTime = (Long) request.getExtra("max_behot_time");
        }
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            String jsData = getJSData("classpath*:js/toutiaoAscp.js");
            engine.eval(jsData);
            if (engine instanceof Invocable) {
                Invocable invocable = (Invocable) engine;
                Map<String, String> result = (Map<String, String>) invocable.invokeFunction("getHoney");
                String as = result.get("as");
                String cp = result.get("cp");
                String realUrl = String.format(AJAX_URL_TMP, maxBehotTime, userId, mid, as, cp);
                request.setUrl(realUrl);
            }

        } catch (Exception e) {
            LOGGER.error("exception for getPage", e);
        }
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


    public static String getJSData(String filePath) {
        try {
            ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = patternResolver.getResources(filePath);
            if (null != resources && resources.length > 0) {
                for (Resource resource : resources) {
                    String fileName = resource.getFilename();
                    URL url = resource.getURL();
                    String jsonData = readFile(url);
                    return jsonData;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("parse js file error:" + filePath, e);
        }
        return null;
    }

    protected WebRequest getWebRequest(Request request, Site site, Map<String, String> headers) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(request.getUrl()));
        selectRequestMethod(webRequest, request);
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                webRequest.setAdditionalHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        if (StringUtils.isNotBlank(site.getUserAgent())) {
            webRequest.setAdditionalHeader("User-Agent", site.getUserAgent());
        }
        if (site.getHttpProxyPool().isEnable()) {
            HttpHost host = site.getHttpProxyFromPool();
            webRequest.setProxyHost(host.getHostName());
            webRequest.setProxyPort(host.getPort());
            request.putExtra(Request.PROXY, host);

        }
        webRequest.setCharset(Charset.forName(site.getCharset()));
        return webRequest;
    }

    protected WebRequest selectRequestMethod(WebRequest webRequest, Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            // default get
            webRequest.setHttpMethod(HttpMethod.GET);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair.length > 0) {
                webRequest.setRequestParameters(toHtmlUnit(nameValuePair));
            }
            webRequest.setHttpMethod(HttpMethod.POST);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            webRequest.setHttpMethod(HttpMethod.HEAD);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            webRequest.setHttpMethod(HttpMethod.PUT);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            webRequest.setHttpMethod(HttpMethod.DELETE);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            webRequest.setHttpMethod(HttpMethod.TRACE);
        }
        return webRequest;
    }

    public static List<com.gargoylesoftware.htmlunit.util.NameValuePair> toHtmlUnit(final NameValuePair[] pairs) {
        final List<com.gargoylesoftware.htmlunit.util.NameValuePair> pairs2 = new ArrayList<com.gargoylesoftware.htmlunit.util.NameValuePair>();
        for (int i = 0; i < pairs.length; i++) {
            final NameValuePair pair = pairs[i];
            com.gargoylesoftware.htmlunit.util.NameValuePair pair2 = new com.gargoylesoftware.htmlunit.util.NameValuePair(pair.getName(), pair.getValue());
            pairs2.add(pair2);
        }
        return pairs2;
    }

    @Override
    public void onSuccess(Request request, Task task) {

    }

    @Override
    public void onFailed(Request request, Task task) {
        HttpHost httpHost = (HttpHost) request.getExtra(Request.PROXY);
        LSProxyPool proxyPool = (LSProxyPool) task.getSite().getHttpProxyPool();
        proxyPool.removeProxy(httpHost.getAddress().getHostAddress());
    }
}
