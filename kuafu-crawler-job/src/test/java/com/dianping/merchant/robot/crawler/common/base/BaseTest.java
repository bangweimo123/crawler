package com.dianping.merchant.robot.crawler.common.base;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.google.gson.Gson;

/**
 * 
 * @author mobangwei
 * 
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:config/spring/common/appcontext-*.xml", "classpath*:config/spring/appcontext-*.xml", "classpath*:config/spring/local/appcontext-*.xml"})
public abstract class BaseTest {
    private static final Gson gson = new Gson();

    protected static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(BaseTest.class);

    protected String logForString(Object data) {
        return gson.toJson(data);
    }

}
