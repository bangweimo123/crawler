package cn.mbw.crawler.core.processor.converter;

import cn.mbw.crawler.core.processor.annotation.CrawlerConverterTag;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.mbw.crawler.core.processor.iface.ICrawlerConverter;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@CrawlerConverterTag(name = "booheeRelationConverter")
public class BooheeRelationConverter implements ICrawlerConverter {
    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        Html html = Html.create((String) sourceData);
        JSONArray result = new JSONArray();
        Selectable item = html.xpath("//ul[@class='list']/li");
        List<Selectable> nodes = item.nodes();
        if (CollectionUtils.isNotEmpty(nodes)) {
            for (int i = 0; i < nodes.size(); i++) {
                Selectable selectable = nodes.get(i);
                String name = selectable.xpath("//a/@href").get();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
                result.add(jsonObject);
            }
        }
        //返回一个list
        return result;
    }
}
