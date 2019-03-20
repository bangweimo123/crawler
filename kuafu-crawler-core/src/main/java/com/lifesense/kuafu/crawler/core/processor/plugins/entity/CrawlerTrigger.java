package com.lifesense.kuafu.crawler.core.processor.plugins.entity;

import java.io.Serializable;

/**
 * 一个spider的定时配置
 * 
 * type simple_once,time,lion
 * 
 * value:(true,false),cronExpression,lionKey
 * 
 * @author mobangwei
 * 
 */
public class CrawlerTrigger implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -8086199653137030685L;
    private String type;

    private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
