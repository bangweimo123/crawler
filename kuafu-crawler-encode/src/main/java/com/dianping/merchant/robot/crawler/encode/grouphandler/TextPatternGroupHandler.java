package com.dianping.merchant.robot.crawler.encode.grouphandler;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

/**
 * 去除标签
 * 
 * @author mobangwei
 * 
 */
public class TextPatternGroupHandler implements IPatternGroupHandler {

    @Override
    public String replacer(Map<Integer, String> groupData, List<Integer> usefulGroup) {
        if (MapUtils.isNotEmpty(groupData)) {
            if (CollectionUtils.isNotEmpty(usefulGroup)) {
                String text = groupData.get(usefulGroup.get(0));
                return text;
            }
        }
        return null;
    }

}
