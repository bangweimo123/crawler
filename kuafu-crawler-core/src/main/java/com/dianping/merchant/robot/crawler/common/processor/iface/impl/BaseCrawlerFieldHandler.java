package com.dianping.merchant.robot.crawler.common.processor.iface.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Xpath2Selector;

import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;
import com.dianping.merchant.robot.crawler.common.processor.converter.ConverterFactory;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConverter;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFieldHandler;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.IFieldBuilder;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.dianping.merchant.robot.crawler.common.processor.utils.JavaScriptExcutorUtils;

/**
 * 基本的field处理器
 * 
 * @author mobangwei
 * 
 */
public class BaseCrawlerFieldHandler implements ICrawlerFieldHandler {

    @Override
    public ProStatus pageField(Page page, List<IFieldBuilder> fieldBuilders) {
        for (IFieldBuilder fieldBuilder : fieldBuilders) {
            Object result = null;
            String fieldName = fieldBuilder.getFieldName();
            Html html = page.getHtml();
            Selectable selectable = null;
            if (StringUtils.isNotBlank(fieldBuilder.getSpForUrlRegex())) {
                String url = page.getRequest().getUrl();
                boolean isMatch = url.matches(fieldBuilder.getSpForUrlRegex());
                if (!isMatch) {
                    continue;
                }
            }
            if (StringUtils.isNotBlank(fieldBuilder.getCssSelector())) {
                if (StringUtils.isBlank(fieldBuilder.getCssSelectorAttrName())) {
                    selectable = html.$(fieldBuilder.getCssSelector());
                } else {
                    selectable = html.$(fieldBuilder.getCssSelector(), fieldBuilder.getCssSelectorAttrName());
                }
            }
            if (StringUtils.isNotBlank(fieldBuilder.getXpathSelector())) {
                selectable = html.xpath(fieldBuilder.getXpathSelector());
            }
            if (StringUtils.isNotBlank(fieldBuilder.getXpath2Selector())) {
                Xpath2Selector xpath2Selector = new Xpath2Selector(fieldBuilder.getXpath2Selector());
                selectable = html.selectList(xpath2Selector);
            }
            if (StringUtils.isNotBlank(fieldBuilder.getRegex())) {
                selectable = selectable.regex(fieldBuilder.getRegex());
            }
            if (null != selectable) {
                if (fieldBuilder.isList()) {
                    result = selectable.all();
                } else {
                    result = selectable.get();
                }
            }
            if (StringUtils.isNotBlank(fieldBuilder.getScript())) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_DATA, result);
                params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_METHODNAME, CrawlerCommonConstants.JavaScriptConstant.DEFAULT_METHODNAME);
                Object targetData = JavaScriptExcutorUtils.eval(fieldBuilder.getScript(), params, page);
                result = targetData;
            }
            if (StringUtils.isNotBlank(fieldBuilder.getConverter())) {
                Object converterParam = fieldBuilder.getConverterParam();
                ICrawlerConverter converter = ConverterFactory.getConverter(fieldBuilder.getConverter());
                result = converter.converter(result, converterParam);
            }
            //设置默认值
            if (StringUtils.isNotBlank(fieldBuilder.getDefaultValue())) {
                if (null == result) {
                    result = fieldBuilder.getDefaultValue();
                }
                if (result instanceof String) {
                    if (StringUtils.isBlank((String) result)) {
                        result = fieldBuilder.getDefaultValue();
                    }
                }
            }
            page.putField(fieldName, result);
        }
        return ProStatus.success();
    }

}
