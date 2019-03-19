package com.dianping.merchant.robot.crawler.encode.grouphandler;

import org.apache.commons.lang.StringUtils;

public class ParamRemovePatternGroupHandler extends SimplePatternGroupHandler {

    @Override
    public String doReplacer(Integer group, String data) {
        String returnData = StringUtils.EMPTY;
        switch (group) {
            case 3:
                returnData = doHandlerParam(data);
                break;
        }
        return returnData;
    }

    private String doHandlerParam(String data) {
        return StringUtils.EMPTY;
    }
}
