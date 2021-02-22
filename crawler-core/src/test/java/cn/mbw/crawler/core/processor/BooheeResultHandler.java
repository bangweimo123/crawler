package cn.mbw.crawler.core.processor;

import cn.mbw.crawler.core.processor.plugins.entity.ProStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import cn.mbw.crawler.core.constants.CrawlerCommonConstants;
import cn.mbw.crawler.core.processor.iface.ICrawlerResultHandler;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

@Component
public class BooheeResultHandler implements ICrawlerResultHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooheeResultHandler.class);
//    @Resource
//    private CrawlerDataService crawlerDataService;

    @Override
    public ProStatus handler(Map<String, Object> resultFields, Map<String, Object> contextParams) {
        Map<String, Object> customParams = (Map<String, Object>) contextParams.get(CrawlerCommonConstants.ProcessorContextConstant.CUSTOM_PARAMS);
        String batch = "";
        if (customParams.containsKey("batch")) {
            batch = ((Integer) customParams.get("batch")).toString();
        }
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> resultField : resultFields.entrySet()) {
            String key = resultField.getKey();
            if (StringUtils.equalsIgnoreCase(key, "crawler-context") || StringUtils.equalsIgnoreCase(key, "proStatus")) {
                continue;
            }
            result.put(key, resultField.getValue());
        }
        String fileName = "/data/webmagic/tmp/boohee-unit%s.csv";
        fileName = String.format(fileName, batch);
        unitToCsv(fileName, resultFields);
        return ProStatus.success();
    }

    public static void contentToTxt(String filePath, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true));
            writer.write("\n" + content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void contentToCsv(String filePath, Map<String, Object> resultFields) {
        String[] headers = {"_id", "fullName", "rename", "enName", "appraise",
                "image", "nutritionInfo", "hot", "nutritionRedGreen", "type", "unit", "relations", "mainSource", "subSource","hotUnit","subSource2","cookMethod"};
        writeCSV(filePath, resultFields, headers);
    }

    public static void unitToCsv(String filePath, Map<String, Object> resultFields) {
        String[] headers = {"_id", "fullName", "hotUnit","subSource2","cookMethod"};
        writeCSV(filePath, resultFields, headers);
    }
    public static void writeCSV(String fileName, Map<String, Object> resultFields, String[] headers) {

        Boolean flag = false;
        //获取文件夹名
        File csvFile = new File(fileName);

        //判断文件是否存在
        if (!csvFile.exists()) {
            flag = true;
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        CsvWriter csvWriter = null;

        try {
            fileWriter = new FileWriter(csvFile, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            //创建CSV写对象
            csvWriter = new CsvWriter(bufferedWriter, ',');

            //如果文件是第一次创建 写入列头
            if (flag == true) {
                csvWriter.writeRecord(headers);
            }

            String[] content = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                Object value = resultFields.get(header);
                if (null != value) {
                    if (value instanceof String) {
                        content[i] = (String) value;
                    } else if (value instanceof JSONObject) {
                        content[i] = ((JSONObject) value).toJSONString();
                    } else if (value instanceof JSONArray) {
                        content[i] = ((JSONArray) value).toJSONString();
                    } else {
                        content[i] = JSON.toJSONString(value);
                    }
                } else {
                    content[i] = "";
                }
            }

            //将List转String数组 写入文件中
            csvWriter.writeRecord(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭写
                bufferedWriter.close();
                fileWriter.close();
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
