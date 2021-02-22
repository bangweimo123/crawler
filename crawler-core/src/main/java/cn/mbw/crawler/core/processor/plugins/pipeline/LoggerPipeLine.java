package cn.mbw.crawler.core.processor.plugins.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 直接日志输出
 *
 * @author mobangwei
 */
public class LoggerPipeLine implements Pipeline {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerPipeLine.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        LOGGER.info("get page: " + resultItems.getRequest().getUrl());
    }
}
