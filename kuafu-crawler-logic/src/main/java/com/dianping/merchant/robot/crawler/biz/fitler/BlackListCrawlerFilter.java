package com.dianping.merchant.robot.crawler.biz.fitler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;

import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerFilter;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProMessageCode;
import com.dianping.merchant.robot.crawler.common.processor.plugins.entity.ProStatus;
import com.google.common.collect.Lists;

/**
 * 黑名单过滤
 * 
 * @author mobangwei
 * 
 */
//@CrawlerFilterTag(priority = 2, type = 2)
public class BlackListCrawlerFilter implements ICrawlerFilter {
    private static List<String> baseKeyWords = Lists.newArrayList("互联网");

    @Override
    public ProStatus doFilter(Page page) {
        String html = page.getRawText();
        Pattern pattern = Pattern.compile(genCompile(baseKeyWords));
        Matcher matcher = pattern.matcher(html);
        if (matcher.matches()) {
            return ProStatus.fail(ProMessageCode.FITLER_BLACKLIST_ERROR.getCode());
        } else {
            return ProStatus.success();
        }
    }

    // ^[\\s\\S]*(互联)*
    private String genCompile(List<String> allKeyWords) {
        StringBuffer sb = new StringBuffer();
        sb.append("(^[\\s\\S]");
        sb.append("*(");
        sb.append("(");
        for (int i = 0; i < allKeyWords.size(); i++) {
            String keyWord = allKeyWords.get(i);
            sb.append(keyWord);
            if (i < allKeyWords.size() - 1) {
                sb.append("|");
            }
        }
        sb.append(")+");
        sb.append(")*");
        sb.append(")");
        return sb.toString();
    }

}
