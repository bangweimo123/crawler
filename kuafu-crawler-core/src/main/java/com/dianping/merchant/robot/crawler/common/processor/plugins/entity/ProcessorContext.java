package com.dianping.merchant.robot.crawler.common.processor.plugins.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;

/**
 * 在processor过程中，这个上下文会向下一直传递
 * 
 * @author mobangwei
 * 
 */
public class ProcessorContext implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1208492115576884139L;

    public static final String CONTEXT_FIELD_NAME = "crawler-context";


    public static ProcessorContext getContext(Page page) {
        ProcessorContext context = page.getResultItems().get(CONTEXT_FIELD_NAME);
        if (null == context) {
            context = new ProcessorContext();
            page.putField(CONTEXT_FIELD_NAME, context);
        }
        return context;
    }

    public static ProcessorContext getContext(ResultItems resultItems) {
        return resultItems.get(CONTEXT_FIELD_NAME);
    }

    private ProStatus proStatus;

    private Map<String, Object> extras = new HashMap<String, Object>();


    public ProcessorContext addParam(String key, Object value) {
        extras.put(key, value);
        return this;
    }

    public ProcessorContext setProStatus(ProStatus proStatus) {
        this.proStatus = proStatus;
        return this;
    }

    public ProStatus getProStatus() {
        return proStatus;
    }

    public Object getParam(String key) {
        return extras.get(key);
    }

    public Map<String, Object> getParams() {
        return extras;
    }
}
