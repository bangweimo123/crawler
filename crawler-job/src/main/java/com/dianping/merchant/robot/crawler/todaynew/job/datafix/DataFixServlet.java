package com.dianping.merchant.robot.crawler.todaynew.job.datafix;

import com.google.common.collect.Maps;
import com.lifesense.kuafu.crawler.core.processor.spring.SpringLocator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataFixServlet extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 2474400446016950129L;

    private final static Logger LOGGER = LoggerFactory.getLogger(DataFixServlet.class);

    private final static String RESULT_NONE = "do nothing";

    private final static String OP_TYPE_ACTION = "action";

    private final ConcurrentLinkedQueue<Map<String, String>> mockParameters = new ConcurrentLinkedQueue<Map<String, String>>();

    private IDataFixService dataFixService;

    public static enum Option {
        fix_url, fix_page_info;
    }

    public static void setBrowserNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setBrowserNoCache(response);
        String pathInfo = request.getPathInfo();
        if (StringUtils.isBlank(pathInfo)) {
            response.getWriter().write(RESULT_NONE);
            return;
        }
        String uri = pathInfo.substring(1);
        String[] ops = uri.split("/");
        if (ops.length == 2) {
            if (OP_TYPE_ACTION.equals(ops[0])) {
                String option = ops[1];
                Option optionEnum = Option.valueOf(option);
                this.doAction(optionEnum, request, response);
            } else {
                response.getWriter().write(RESULT_NONE);
            }
        } else {
            response.getWriter().write(RESULT_NONE);
        }
    }

    private void doAction(Option operation, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> params = request.getParameterMap();
        Map<String, Object> toFixParams = Maps.newHashMap();
        for (String key : params.keySet()) {
            toFixParams.put(key, params.get(key)[0]);
        }
        dataFixService = SpringLocator.getBean(operation.name());
        dataFixService.fix(toFixParams);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest request,
     * javax.servlet.http.HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

}
