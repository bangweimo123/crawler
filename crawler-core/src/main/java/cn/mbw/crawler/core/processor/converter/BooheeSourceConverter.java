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

@CrawlerConverterTag(name = "booheeSourceConverter")
public class BooheeSourceConverter implements ICrawlerConverter {
    @Override
    public Object converter(Page page, Object sourceData, Object params) {
        JSONArray result = new JSONArray();
        if (null != sourceData) {
            Html html = Html.create((String) sourceData);
            if (null != html) {
                Selectable item = html.xpath("//ul/li");
                if (null != item) {
                    List<Selectable> nodes = item.nodes();
                    if (CollectionUtils.isNotEmpty(nodes)) {
                        for (int i = 0; i < nodes.size(); i++) {
                            Selectable selectable = nodes.get(i);
                            String relation = selectable.xpath("/li/a/@href").get();
                            String name = selectable.xpath("/li/a/text()").get();
                            String value = selectable.xpath("/li/text()").get();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", name);
                            jsonObject.put("value", value);
                            jsonObject.put("relation", relation);
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
