package cn.mbw.crawler.core.processor.converter;

import cn.mbw.crawler.core.processor.annotation.CrawlerConverterTag;
import cn.mbw.crawler.core.processor.iface.ICrawlerConverter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@CrawlerConverterTag(name = "booheeUnitConverter")
public class BooheeUnitConverter implements ICrawlerConverter {
    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        JSONArray result = new JSONArray();
        if (null != sourceData) {
            Html html = Html.create((String) sourceData);
            if (null != html) {
                Selectable content = html.xpath("//tbody/tr");
                if (null != content) {
                    List<Selectable> nodes = content.nodes();
                    if (CollectionUtils.isNotEmpty(nodes)) {
                        for (int i = 0; i < nodes.size(); i++) {
                            Selectable selectable = nodes.get(i);
                            String name = selectable.xpath("//tr/td[1]/span/text()").get();
                            if (StringUtils.isBlank(name)) {
                                name = selectable.xpath("//tr/td[1]/text()").get();
                            }
                            if (StringUtils.isBlank(name)) {
                                name = selectable.xpath("//tr/td[1]/a/text()").get();
                            }
                            String value = selectable.xpath("//tr/td[2]/span/text()").get();
                            if (StringUtils.isBlank(value)) {
                                value = selectable.xpath("//tr/td[2]/text()").get();
                            }
                            if (StringUtils.isBlank(value)) {
                                value = selectable.xpath("//tr/td[2]/a/text()").get();
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", name);
                            jsonObject.put("value", value);
                            result.add(jsonObject);
                        }
                    }
                }
            }
        }
        //返回一个list
        return result;
    }
}
