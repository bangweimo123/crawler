package cn.mbw.crawler.core.processor.plugins.entity;

import java.io.Serializable;
import java.util.Map;

import us.codecraft.webmagic.utils.HttpConstant;

/**
 * url处理器,必须序列化，存redis
 *
 * @author mobangwei
 */
public class IUrlBuilder implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1179466109783320140L;
    /**
     * 请求的方法，默认为GET请求，可以设置为POST请求
     */
    public String method = HttpConstant.Method.GET;

    /**
     * 某个url的过滤器
     */
    public String baseUrlFilter;
    /**
     * 该url期望爬取深度
     *
     * @return
     */
    public Integer baseDeep;
    /**
     * url数量设置
     */
    public Integer baseCount;
    /**
     * 针对特定的urlFilter,添加特定的urlFilters
     */
    public Map<String, String> urlFilterPlugins;

    /**
     * 针对特定的详情页第一页，添加分页数据的url
     */
    public Map<String, String> pageUrlFilterPlugins;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBaseUrlFilter() {
        return baseUrlFilter;
    }

    public void setBaseUrlFilter(String baseUrlFilter) {
        this.baseUrlFilter = baseUrlFilter;
    }

    public Integer getBaseDeep() {
        return baseDeep;
    }

    public void setBaseDeep(Integer baseDeep) {
        this.baseDeep = baseDeep;
    }

    public Map<String, String> getUrlFilterPlugins() {
        return urlFilterPlugins;
    }

    public void setUrlFilterPlugins(Map<String, String> urlFilterPlugins) {
        this.urlFilterPlugins = urlFilterPlugins;
    }

    public Map<String, String> getPageUrlFilterPlugins() {
        return pageUrlFilterPlugins;
    }

    public void setPageUrlFilterPlugins(Map<String, String> pageUrlFilterPlugins) {
        this.pageUrlFilterPlugins = pageUrlFilterPlugins;
    }

    public Integer getBaseCount() {
        return baseCount;
    }

    public void setBaseCount(Integer baseCount) {
        this.baseCount = baseCount;
    }
}
