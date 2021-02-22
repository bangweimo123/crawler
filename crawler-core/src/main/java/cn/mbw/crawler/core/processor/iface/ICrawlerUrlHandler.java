package cn.mbw.crawler.core.processor.iface;

import cn.mbw.crawler.core.processor.plugins.entity.IUrlBuilder;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import us.codecraft.webmagic.Page;

import java.util.List;
import java.util.Map;

/**
 * url处理器
 *
 * @author mobangwei
 */
public interface ICrawlerUrlHandler {
    public ProStatus handler(Page page, IUrlBuilder urlBuilder);

    void addTargetRequests(Page page, List<String> requestStrings, Map<String, Object> extras, String method);

    void addTargetRequest(Page page, String requestString, Map<String, Object> extras, String method);

    void addTargetRequestWithoutCanno(Page page, String requestString, Map<String, Object> extras, String method);
}
