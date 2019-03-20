package com.lifesense.kuafu.crawler.core.processor.converter;

import com.lifesense.kuafu.crawler.core.processor.annotation.CrawlerConverterTag;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.*;
import org.htmlcleaner.conditional.ITagNodeCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对html的某些标签进行过滤的一个转换器
 *
 * @author mobangwei
 */
@CrawlerConverterTag(name = "htmlTagFilterConverter")
public class HtmlTagFilterConverter implements ICrawlerConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlTagFilterConverter.class);
    private static final String exclude_tag = "excludeTag";

    private static final String exculde_xpath = "excludeXpath";

    @Override
    public Object converter(Object sourceData, Object params) {
        if (null == sourceData) {
            return null;
        }
        String content = (String) sourceData;
        if (null != params) {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> paramMap = (Map<String, List<String>>) params;
            List<String> excludeTag = paramMap.get(exclude_tag);
            List<String> excludeXpath = paramMap.get(exculde_xpath);
            CleanerProperties cleanerProperties = new CleanerProperties();
            cleanerProperties.setPruneTags(StringUtils.join(excludeTag, ","));
            cleanerProperties.addPruneTagNodeCondition(new XpatherTagNodeCondition(getAllTagNodesByXpath(content, excludeXpath)));
            cleanerProperties.setOmitXmlDeclaration(true);
            HtmlCleaner htmlCleaner = new HtmlCleaner(cleanerProperties);

            TagNode tagNode = htmlCleaner.clean((String) sourceData);
            try {
                tagNode = (TagNode) (tagNode.evaluateXPath("//body")[0]);
            } catch (XPatherException e) {
                LOGGER.warn("path body error");
            }
            content = new SimpleHtmlSerializer(cleanerProperties).getAsString(tagNode, true);
        }
        return content;
    }

    public class XpatherTagNodeCondition implements ITagNodeCondition {
        private List<TagNode> pruneTagNodes;

        public XpatherTagNodeCondition(List<TagNode> pruneTagNodes) {
            this.pruneTagNodes = pruneTagNodes;
        }

        @Override
        public boolean satisfy(TagNode tagNode) {
            try {
                for (TagNode prunTagNode : pruneTagNodes) {
                    if (isEqualNode(prunTagNode, tagNode)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            return false;
        }
    }

    public boolean isEqualNode(TagNode source, TagNode target) {
        if (source == target) {
            return true;
        }
        if (source.getText() == null) {
            if (target.getText() != null) {
                return false;
            }
        } else if (!source.getText().toString().equals(target.getText().toString())) {
            return false;
        }
        if (source.getCol() != target.getCol()) {
            return false;
        }
        if (source.getRow() != target.getRow()) {
            return false;
        }
        // in theory nodeName can't be null but better be careful
        // who knows what other implementations may be doing?...
        if (source.getName() == null) {
            if (target.getName() != null) {
                return false;
            }
        } else if (!source.getName().equals(target.getName())) {
            return false;
        }
        Map<String, String> sourceNameSpace = source.getNamespaceDeclarations();
        Map<String, String> targetNameSpace = target.getNamespaceDeclarations();
        if (null == sourceNameSpace) {
            if (null != targetNameSpace) {
                return false;
            }
        } else {
            if (sourceNameSpace.size() != targetNameSpace.size()) {
                return false;
            }
            for (String key : sourceNameSpace.keySet()) {
                if (null == sourceNameSpace.get(key)) {
                    if (null != targetNameSpace.get(key)) {
                        return false;
                    }
                } else {
                    if (!sourceNameSpace.get(key).equals(targetNameSpace.get(key))) {
                        return false;
                    }
                }
            }
        }
        Map<String, String> sourceAttr = source.getAttributes();
        Map<String, String> targetAttr = target.getAttributes();
        if (null == sourceAttr) {
            if (null != targetAttr) {
                return false;
            }
        } else {
            if (sourceAttr.size() != targetAttr.size()) {
                return false;
            }
            for (String key : sourceAttr.keySet()) {
                if (null == sourceAttr.get(key)) {
                    if (null != targetAttr.get(key)) {
                        return false;
                    }
                } else {
                    if (!sourceAttr.get(key).equals(targetAttr.get(key))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private List<TagNode> getAllTagNodesByXpath(String content, List<String> xpaths) {
        try {
            if (CollectionUtils.isNotEmpty(xpaths)) {
                List<TagNode> tagNodes = new ArrayList<TagNode>();
                HtmlCleaner htmlCleaner = new HtmlCleaner();
                TagNode contentTagNode = htmlCleaner.clean(content);
                for (String xpath : xpaths) {
                    XPather xpather = new XPather(xpath);
                    Object[] xpathTagNodes = xpather.evaluateAgainstNode(contentTagNode);
                    for (Object xpathTagNode : xpathTagNodes) {
                        tagNodes.add((TagNode) xpathTagNode);
                    }
                }
                return tagNodes;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }
}
