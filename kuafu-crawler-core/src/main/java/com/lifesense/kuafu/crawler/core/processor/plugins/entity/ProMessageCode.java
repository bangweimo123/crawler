package com.lifesense.kuafu.crawler.core.processor.plugins.entity;

/**
 * 返回消息集合
 * 
 * @author mobangwei
 * 
 */
public enum ProMessageCode {

    SUCCESS(200, "成功"),
    
    LOCK(1000,"数据库锁"),

    DEFAULT(0,"默认code"),
    
    FITLER_ERROR(100, "过滤器失败"),

    FITLER_KEYWORD_ERROR(1001, "关键词匹配失败"),

    FITLER_BLACKLIST_ERROR(1002, "黑名单匹配"),

    FITLER_REDIRECT_ERROR(1003, "重定向url需要过滤"),

    FITLER_LIMITDATE_ERROR(1004, "日期过滤失败"),

    FIELD_PARSE_ERROR(101, "字段解析错误"),

    URL_PARSE_ERROR(102, "地址解析错误"),

    URL_DONT_NEED_PARSE(103, "地址不需要解析,但是需要传递"),

    RESULT_PARSER_ERROR(105, "结果处理失败"),

    DOWNLOAD_ERROR(106,"页面下载失败");
    private ProMessageCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;

    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
