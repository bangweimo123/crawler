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

@CrawlerConverterTag(name = "booheeNutritionConverter")
public class BooheeNutritionConverter implements ICrawlerConverter {
    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        Html html = Html.create((String) sourceData);
        JSONArray result = new JSONArray();
        Selectable content = html.xpath("//div[@class='content']/dl");
        List<Selectable> nodes = content.nodes();
        Selectable moreInfo = html.xpath("//div[@class='content']/div[@class='more-attribute hide']/dl");
        List<Selectable> moreNodes = moreInfo.nodes();
        if (CollectionUtils.isNotEmpty(moreNodes)) {
            nodes.addAll(moreNodes);
        }
        if (CollectionUtils.isNotEmpty(nodes)) {
            for (int i = 1; i < nodes.size(); i++) {
                Selectable selectable = nodes.get(i);
                List<Selectable> itemNodes = selectable.xpath("//dd").nodes();
                if (CollectionUtils.isNotEmpty(itemNodes)) {
                    for (Selectable itemSelectable : itemNodes) {
                        String name = itemSelectable.xpath("//span[@class='dt']/text()").get();
                        String value = itemSelectable.xpath("//span[@class='dd']/text()").get();
                        if (StringUtils.isBlank(value)) {
                            value = itemSelectable.xpath("//span[@class='dd']/span[@class='stress red1']/text()").get();
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", name);
                        jsonObject.put("value", value);
                        result.add(jsonObject);
                    }
                }
            }
        }
        //返回一个list
        return result;
    }
}
