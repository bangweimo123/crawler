package com.lifesense.kuafu.crawler.core.base;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;

public class JUnit4ClassRunner extends SpringJUnit4ClassRunner {

    public JUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

}  