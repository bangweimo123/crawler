package com.dianping.merchant.robot.crawler.common.processor.iface;

import java.util.List;

import us.codecraft.webmagic.Page;

import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.IFieldBuilder;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;

/**
 * 输出处理器
 * 
 * @author mobangwei
 * 
 */
public interface ICrawlerFieldHandler {

    public ProStatus pageField(Page page, List<IFieldBuilder> fieldBuilders);

}
