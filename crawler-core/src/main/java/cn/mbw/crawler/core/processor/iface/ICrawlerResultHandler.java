package cn.mbw.crawler.core.processor.iface;

import java.util.Map;

import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;

/**
 * 针对返回的结果集做处理
 *
 * @author mobangwei
 *
 */
public interface ICrawlerResultHandler {
    /**
     * 将结果集解析后处理
     *
     * @param resultFields
     */
    public ProStatus handler(Map<String, Object> resultFields, Map<String, Object> contextParams);
}
