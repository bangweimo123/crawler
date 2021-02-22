package cn.mbw.crawler.core.processor.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringLocator implements ApplicationContextAware {
    private static ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ac;
    }

    public static <T> T getBean(String beanName) {
        return (T) ac.getBean(beanName);
    }
}
