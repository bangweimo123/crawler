package com.dianping.merchant.robot.crawler.common.processor.utils;

import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.merchant.robot.crawler.common.constants.CrawlerCommonConstants;

public class JavaScriptExcutorUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptExcutorUtils.class);
    private static ScriptEngineManager engineManager = new ScriptEngineManager();
    private static ScriptEngine scriptEngine = engineManager.getEngineByName("javascript");


    /**
     * 通过js脚本将结果集做过滤
     * 
     * @param script
     * @param params
     * @param page
     * @return
     */
    public synchronized static Object eval(String script, Map<String, Object> params, Page page) {
        try {
            ScriptContext context = new SimpleScriptContext();
            context.setAttribute(CrawlerCommonConstants.JavaScriptConstant.PARAM_PAGE, page, ScriptContext.GLOBAL_SCOPE);
            scriptEngine.eval(script);
            if (scriptEngine instanceof Invocable) {
                Invocable invoke = (Invocable) scriptEngine; // 调用merge方法，并传入两个参数
                String methodName = (String) params.get(CrawlerCommonConstants.JavaScriptConstant.PARAM_METHODNAME);
                Object data = params.get(CrawlerCommonConstants.JavaScriptConstant.PARAM_DATA);
                Object result = invoke.invokeFunction(methodName, data);
                return result;
            }
        } catch (Exception e) {
            LOGGER.error("eval script error for script:" + script);
        }
        return null;
    }
}
