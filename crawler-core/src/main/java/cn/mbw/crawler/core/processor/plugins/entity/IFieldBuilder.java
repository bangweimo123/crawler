package cn.mbw.crawler.core.processor.plugins.entity;

import java.io.Serializable;

public class IFieldBuilder implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 643447177741423237L;
    /**
     * 针对特定的url
     */
    private String spForUrlRegex;

    private String cssSelector;

    private String cssSelectorAttrName;

    private String xpathSelector;

    private String xpath2Selector;

    private String fieldName;

    private String defaultValue;

    private Boolean isList;

    private String regex;
    /**
     * 内容处理器,即将获取到的内容进行重新编写逻辑
     */
    private String script;

    /**
     * converter方法
     */
    private String converter;

    private Object converterParam;

    public String getSpForUrlRegex() {
        return spForUrlRegex;
    }

    public void setSpForUrlRegex(String spForUrlRegex) {
        this.spForUrlRegex = spForUrlRegex;
    }

    public Boolean getIsList() {
        return isList;
    }

    public void setIsList(Boolean isList) {
        this.isList = isList;
    }

    public String getCssSelector() {
        return cssSelector;
    }

    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    public String getCssSelectorAttrName() {
        return cssSelectorAttrName;
    }

    public void setCssSelectorAttrName(String cssSelectorAttrName) {
        this.cssSelectorAttrName = cssSelectorAttrName;
    }

    public String getXpath2Selector() {
        return xpath2Selector;
    }

    public void setXpath2Selector(String xpath2Selector) {
        this.xpath2Selector = xpath2Selector;
    }

    public String getXpathSelector() {
        return xpathSelector;
    }

    public void setXpathSelector(String xpathSelector) {
        this.xpathSelector = xpathSelector;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean isList) {
        this.isList = isList;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getConverter() {
        return converter;
    }

    public void setConverter(String converter) {
        this.converter = converter;
    }

    public Object getConverterParam() {
        return converterParam;
    }

    public void setConverterParam(Object converterParam) {
        this.converterParam = converterParam;
    }
}
