package cn.mbw.crawler.core.processor.plugins.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 针对一种页面的处理
 *
 * @author mobangwei
 *
 */
public class BaseCrawlerConfig implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5802391353976375575L;

    private IUrlBuilder urlBuilder;

    private List<IFieldBuilder> fieldBuilder;


    public IUrlBuilder getUrlBuilder() {
        return urlBuilder;
    }

    public void setUrlBuilder(IUrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    public List<IFieldBuilder> getFieldBuilder() {
        return fieldBuilder;
    }

    public void setFieldBuilder(List<IFieldBuilder> fieldBuilder) {
        this.fieldBuilder = fieldBuilder;
    }
}
