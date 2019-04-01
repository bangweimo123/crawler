package com.lifesense.kuafu.crawler.encode.html;

import com.lifesense.kuafu.crawler.encode.grouphandler.IPatternGroupHandler;
import com.lifesense.kuafu.crawler.encode.grouphandler.ImgUrlPatternGroupHandler;
import com.lifesense.kuafu.crawler.encode.tag.parser.TagParserEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParserUtils {
    public static String parser(String html, TagParserEnum tagParserEnum) {
        String patternStr = tagParserEnum.getPattern();
        Pattern pattern = Pattern.compile(patternStr);
        return parser(html, pattern, tagParserEnum.getGroups(), tagParserEnum.getGroupHandler());
    }

    /**
     * @param html
     * @return
     */
    public static String parser(String html, Pattern pattern, Integer[] groups, IPatternGroupHandler groupHandler) {

        StringBuilder stringBuilder = new StringBuilder();
        Matcher matcher = pattern.matcher(html);
        int lastEnd = 0;
        boolean modified = false;
        while (matcher.find()) {
            modified = true;
            stringBuilder.append(StringUtils.substring(html, lastEnd, matcher.start()));
            Map<Integer, String> groupData = new LinkedHashMap<Integer, String>();
            for (Integer group = 1; group <= matcher.groupCount(); group++) {
                String data = matcher.group(group);
                groupData.put(group, data);
            }
            String groupHandlerResult = groupHandler.replacer(groupData, Arrays.asList(groups));
            stringBuilder.append(groupHandlerResult);
            lastEnd = matcher.end();
        }
        if (!modified) {
            return html;
        }
        stringBuilder.append(StringUtils.substring(html, lastEnd));
        return stringBuilder.toString();

    }
}
