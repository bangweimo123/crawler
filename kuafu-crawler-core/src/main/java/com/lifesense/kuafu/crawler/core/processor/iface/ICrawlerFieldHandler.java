package com.lifesense.kuafu.crawler.core.processor.iface;

import java.util.List;

import us.codecraft.webmagic.Page;

import com.lifesense.kuafu.crawler.core.processor.plugins.entity.IFieldBuilder;
import com.lifesense.kuafu.crawler.core.processor.plugins.entity.ProStatus;

/**
 * 输出处理器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerFieldHandler {

    public ProStatus pageField(Page page, List<IFieldBuilder> fieldBuilders);

}
