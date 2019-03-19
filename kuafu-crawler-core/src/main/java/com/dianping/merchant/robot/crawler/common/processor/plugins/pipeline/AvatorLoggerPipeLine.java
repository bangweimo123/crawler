package com.dianping.merchant.robot.crawler.common.processor.plugins.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;

/**
 * 直接日志输出
 * 
 * @author mobangwei
 * 
 */
public class AvatorLoggerPipeLine implements Pipeline {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(AvatorLoggerPipeLine.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        LOGGER.info("get page: " + resultItems.getRequest().getUrl());
    }
}
