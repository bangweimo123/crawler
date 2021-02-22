package cn.mbw.crawler.core.processor.plugins.downloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.mbw.crawler.core.processor.annotation.CrawlerDownloaderTag;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.HttpConstant;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;

@CrawlerDownloaderTag(name = "jsDownloader")
public class HtmlUnitHttpClientDownloader extends HttpClientDownloader {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static WebClient initWebClient() {
        // 创建一个webclient
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(6000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getCookieManager().setCookiesEnabled(true);

        return webClient;
    }

    protected void doHandlerWebClient(WebClient webClient) {
        int count = Integer.MAX_VALUE;
        while (count != 0) {
            count = webClient.waitForBackgroundJavaScript(600 * 1000);
        }
    }

    @Override
    public Page download(Request request, Task task) {
        WebClient webClient = initWebClient();
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
         logger.info("downloading page {}", request.getUrl());
        WebResponse webResponse = null;
        int statusCode = 0;
        try {
            WebRequest webRequest = getWebRequest(request, site, headers);
            webRequest.setCharset(Charset.forName(charset));

            doHandlerWebClient(webClient);
            HtmlPage htmlunitPage = webClient.getPage(webRequest);
            webClient.waitForBackgroundJavaScript(20000);
            webResponse = htmlunitPage.getWebResponse();
            statusCode = webResponse.getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, htmlunitPage);
                onSuccess(request);
                return page;
            } else {
                logger.warn("code error " + statusCode + "\t" + request.getUrl());
                return null;
            }
        } catch (IOException e) {
            logger.warn("download page " + request.getUrl() + " error", e);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
            onError(request);
            return null;
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                webClient.close();
            } catch (Exception e) {
                logger.warn("close response fail", e);
            }
        }
    }

    protected Page handleResponse(Request request, HtmlPage htmlPage) throws IOException {
        Page page = new Page();
        page.setRawText(htmlPage.asXml());
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode((Integer) request.getExtra(Request.STATUS_CODE));
        return page;
    }

    protected String getContent(String charset, WebResponse webResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(webResponse.getContentAsStream());

            Charset htmlCharset = webResponse.getContentCharsetOrNull();
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset.name());
            } else {
                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(webResponse.getContentAsStream(), charset);
        }
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
}
