package com.lifesense.kuafu.crawler.core.constants;

public class CrawlerCommonConstants {

    public static class FilterConstant {

        public static final int PRE_FILTER_TYPE = 1;

        public static final int MID_FILTER_TYPE = 2;

        public static final int AFTER_FILTER_TYPE = 3;

    }

    public static class ProxyBaseConstants {
        public static final long PERIOD = 10800;
        public static final int SCORE = 1;
    }

    public static class JavaScriptConstant {
        public static final String PARAM_PAGE = "page";
        public static final String PARAM_METHODNAME = "methodName";
        public static final String PARAM_DATA = "data";
        public static final String DEFAULT_METHODNAME = "dataConverter";
    }

    public static class ProcessorContextConstant {
        public static final String BASE_URL = "baseUrl";
        public static final String CURRENT_URL = "currentUrl";
        public static final String DOMAIN_TAG = "domainTag";
        public static final String LIMIT_MONTH = "limitMonth";
        public static final String CUSTOM_PARAMS = "customParams";
    }


    public static class URLBuilderConstant {
        public static final String URL_BUILDER_NAME = "urlBuilder";
        public static final String RELATION_DATA = "relationData";
    }

    public static class PageGroupConstant {
        public static final String PAGE_GROUP = "pageGroup";
        public static final String PAGE_GROUP_INDEX = "pageGroupIndex";
        public static final String PAGE_GROUP_REGEX = "pageGroupRegex";
    }

    public static class RequestParamConstant {
        public static final String NAME_VALUE_PAIR = "nameValuePair";
    }
}
