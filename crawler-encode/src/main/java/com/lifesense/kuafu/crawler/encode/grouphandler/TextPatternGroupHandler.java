package com.lifesense.kuafu.crawler.encode.grouphandler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * 去除标签
 *
 * @author mobangwei
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
