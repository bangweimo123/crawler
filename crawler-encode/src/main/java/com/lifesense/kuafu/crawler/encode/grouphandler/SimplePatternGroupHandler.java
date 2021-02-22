package com.lifesense.kuafu.crawler.encode.grouphandler;


import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public abstract class SimplePatternGroupHandler implements IPatternGroupHandler {
    @Override
    public String replacer(Map<Integer, String> groupData, List<Integer> usefulGroup) {
        if (MapUtils.isNotEmpty(groupData)) {
            StringBuffer sb = new StringBuffer();
            for (Integer simpleGroup : groupData.keySet()) {
                String data = groupData.get(simpleGroup);
                if (usefulGroup.contains(simpleGroup)) {
                    data = doReplacer(simpleGroup, data);
                }
                sb.append(data);
            }
            return sb.toString();
        }
        return null;
    }

    public abstract String doReplacer(Integer group, String data);
}
