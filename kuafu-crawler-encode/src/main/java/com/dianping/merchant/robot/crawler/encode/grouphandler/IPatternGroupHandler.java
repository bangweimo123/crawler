package com.dianping.merchant.robot.crawler.encode.grouphandler;

import java.util.List;
import java.util.Map;

/**
 * 针对所有需要替换的group的数据做替换
 * 
 * @author mobangwei
 * 
 */
public interface IPatternGroupHandler {
    /**
     * 参数说明:
     * 
     * groupData表示通过正则后获取的数据
     * 
     * 返回结果为新的数据
     * 
     * @param groupData
     * @return
     */
    public String replacer(Map<Integer, String> groupData, List<Integer> usefulGroup);
}
