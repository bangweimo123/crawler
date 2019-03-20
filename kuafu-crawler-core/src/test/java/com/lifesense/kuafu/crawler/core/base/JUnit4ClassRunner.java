package com.lifesense.kuafu.crawler.core.base;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class JUnit4ClassRunner extends SpringJUnit4ClassRunner {

    public JUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

}  