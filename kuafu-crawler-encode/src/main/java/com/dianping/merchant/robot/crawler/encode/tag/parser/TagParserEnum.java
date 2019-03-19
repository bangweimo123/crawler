package com.dianping.merchant.robot.crawler.encode.tag.parser;

import com.dianping.merchant.robot.crawler.encode.grouphandler.IPatternGroupHandler;
import com.dianping.merchant.robot.crawler.encode.grouphandler.ImgUrlPatternGroupHandler;
import com.dianping.merchant.robot.crawler.encode.grouphandler.ParamRemovePatternGroupHandler;
import com.dianping.merchant.robot.crawler.encode.grouphandler.TextPatternGroupHandler;

public enum TagParserEnum {
    // 将a标签去除
    a_parser_text("(<a[^>]*href=[\"'])([^\"']*)([\"'][^>]*>)([\\w\\W]*?)(</a>)", new Integer[] {4}, new TextPatternGroupHandler()),

    // 替换img标签的url
    img_parser_url("(<img[^>]*src=[\"'])([^\"']*)([\"'])([^>]*)(/>)", new Integer[] {2}, new ImgUrlPatternGroupHandler()),

    a_remove_param("(<a[^<>]*href=[\"'])([^\\?\"']*)(\\?.*)([\"'][^>]*>)", new Integer[] {3}, new ParamRemovePatternGroupHandler());
    private String pattern;

    private Integer[] groups;

    private IPatternGroupHandler groupHandler;

    private TagParserEnum(String pattern, Integer[] groups, IPatternGroupHandler groupHandler) {
        this.pattern = pattern;
        this.groups = groups;
        this.groupHandler = groupHandler;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer[] getGroups() {
        return groups;
    }

    public void setGroups(Integer[] groups) {
        this.groups = groups;
    }

    public IPatternGroupHandler getGroupHandler() {
        return groupHandler;
    }

    public void setGroupHandler(IPatternGroupHandler groupHandler) {
        this.groupHandler = groupHandler;
    }
}
