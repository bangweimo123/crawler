package cn.mbw.crawler.core.processor.iface;

import java.util.List;

import cn.mbw.crawler.core.processor.plugins.entity.IFieldBuilder;
import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import us.codecraft.webmagic.Page;

/**
 * 输出处理器
 *
 * @author mobangwei
 *
 */
public interface ICrawlerFieldHandler {

    public ProStatus pageField(Page page, List<IFieldBuilder> fieldBuilders);

}
