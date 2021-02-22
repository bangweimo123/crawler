package cn.mbw.crawler.core.processor.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 针对过滤器的一个分类和排序的注解
 *
 * @author mobangwei
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Component
public @interface CrawlerFilterTag {
    /**
     * 优先级，1-100越小表示优先度越高,1-10为默认过滤器的级别，自定义的从11开始
     *
     * @return
     */
    int priority();

    /**
     * 类型,1-pre,2-middle,3-after
     *
     * @return
     */
    int type();

    /**
     * 目标的domainTag,如果为空，表示是通用过滤器
     *
     * @return
     */
    String[] target() default {};
}
