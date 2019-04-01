package com.lifesense.kuafu.crawler.core.base;

import com.google.gson.Gson;
import com.lifesense.base.spring.InstanceFactory;
import com.lifesense.base.spring.SpringInstanceProvider;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author mobangwei
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:config/spring/common/appcontext-*.xml"})
public abstract class BaseTest implements ApplicationContextAware {

    private static final Gson gson = new Gson();

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    static {
        initLogger();
    }

    protected String logForString(Object data) {
        return gson.toJson(data);
    }

    protected static void initLogger() {
        try {
            File file = new File("src/main/resources/log4j2.xml");
            ConfigurationSource source = new ConfigurationSource(new FileInputStream(file), file);
            Configurator.initialize(Thread.currentThread().getContextClassLoader(), source);
            LOGGER.info("initLogger success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        InstanceFactory.setInstanceProvider(new SpringInstanceProvider(arg0));
    }
}
