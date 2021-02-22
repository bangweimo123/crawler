package cn.mbw.crawler.core.processor.utils;

import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;

import javax.script.*;
import java.util.Map;

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
