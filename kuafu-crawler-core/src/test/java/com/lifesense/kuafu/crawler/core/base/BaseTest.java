package com.lifesense.kuafu.crawler.core.base;

import com.google.gson.Gson;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author mobangwei
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:config/spring/common/appcontext-*.xml"})
public abstract class BaseTest {
    private static final Gson gson = new Gson();

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    protected String logForString(Object data) {
        return gson.toJson(data);
    }

}
