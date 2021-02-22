package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.iface.ICrawlerProxyBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LSCrawlerProxyBuilder implements ICrawlerProxyBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ICrawlerProxyBuilder.class);
    private static final String HTTP_URL = "http://piping.mogumiao.com/proxy/api/get_ip_bs?appKey=921815fb9c6841b2a1c11a41d5664eeb&count=%s&expiryDate=0&format=2&newLine=2";
    private static CloseableHttpClient client = HttpClients.createDefault();

    @Override
    public List<String[]> builder(Integer count) {
        List<String[]> resultList = new ArrayList<>();
        try {
            String REAL_URL = String.format(HTTP_URL, count);
            HttpGet httpGet = new HttpGet(REAL_URL);
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            if (StringUtils.isNotBlank(result)) {
                String[] ipPortList = result.split("\r\n");
                if (null != ipPortList && ipPortList.length > 0) {
                    for (String ipPort : ipPortList) {
                        String[] ipPortPair = ipPort.split(":");
                        resultList.add(ipPortPair);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("exception for ipPort load", e);
        }
        return resultList;
    }


}
