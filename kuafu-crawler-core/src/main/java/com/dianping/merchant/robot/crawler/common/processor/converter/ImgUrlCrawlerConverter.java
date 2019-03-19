package com.dianping.merchant.robot.crawler.common.processor.converter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.lion.client.ConfigCache;
import com.dianping.merchant.robot.crawler.common.processor.annotation.CrawlerConverterTag;
import com.dianping.merchant.robot.crawler.common.processor.iface.ICrawlerConverter;
import com.dianping.piccenter.upload.api.HttpUploadAPI;
import com.dianping.piccenter.upload.api.TokenBuilder;
import com.dianping.piccenter.upload.api.UploadPicFlowType;
import com.dianping.piccenter.upload.api.UploadPicToken;

/**
 * 针对图片的一个转换器
 * 
 * @author mobangwei
 * 
 */
@CrawlerConverterTag(name = "imgConverter")
public class ImgUrlCrawlerConverter implements ICrawlerConverter {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(ImgUrlCrawlerConverter.class);

    @Override
    public Object converter(Object sourceData,Object params) {
        if (null != sourceData) {
            String imgUrl = (String) sourceData;
            if (isLegalUrl(imgUrl)) {
                byte[] imgByte = downloadImg(imgUrl);
                String newImgUrl = upload(imgByte, imgUrl);
                return newImgUrl;
            }
        }
        return null;
    }


    private boolean isLegalUrl(String url) {
        return url.matches("http://([^']+(?:jpg|gif|png|bmp|jpeg))(\\S+)?");
    }

    /**
     * 将下载的图片上传，返回一个新的地址
     * 
     * @param fileStream
     * @return
     */
    public static String upload(byte[] picBytes, String fileName) {
        String url = null;
        TokenBuilder tokenBuilder = new TokenBuilder();

        UploadPicToken uploadPicToken = tokenBuilder.setUserId(UplaodConstant.UPLOAD_USER_ID).setClientType(UplaodConstant.CLIENT_TYPE).setBizId(UplaodConstant.UPLOAD_BIZ_ID).setUploadPicFlow(UploadPicFlowType.UGC_PIC_HIDE.value).getToken();

        HttpUploadAPI httpUploadAPI = new HttpUploadAPI(UplaodConstant.UPLOAD_BIZ_ID, UplaodConstant.UPLOAD_SECRENT_KEY);
        httpUploadAPI.setReadTimeout(UplaodConstant.UPLOAD_READ_TIMEOUT); // 可以自定义，默认5s
        httpUploadAPI.setConnectTimeout(UplaodConstant.UPLOAD_CONNECT_TIMEOUT); // 可以自定义，默认5s
        httpUploadAPI.setRequestURL(ConfigCache.getInstance().getProperty("piccenter-upload.server.vip"));

        try {
            Map<String, String> actual = httpUploadAPI.execute(uploadPicToken, picBytes, fileName, null); // 分别传入token对象、文件对象和文件名
            url = actual.get("url");
        } catch (Exception e) {
            LOGGER.error("httpUploadAPI error", e);
        }
        return url;
    }

    private static class UplaodConstant {
        public static final Integer CLIENT_TYPE = 0;
        public static final Integer UPLOAD_USER_ID = Integer.parseInt("0019411");
        public static final Integer UPLOAD_BIZ_ID = 109;
        public static final Integer UPLOAD_READ_TIMEOUT = 10000;
        public static final Integer UPLOAD_CONNECT_TIMEOUT = 6000;
        public static final String UPLOAD_SECRENT_KEY = "713549987bfb175c96606ebf4d99845c";
    }

    /**
     * 获取图片字节流
     * 
     * @param uri
     * @return
     * @throws Exception
     */
    public static byte[] downloadImg(String uri) {
        CloseableHttpClient client = HttpClientBuilder.create().setConnectionTimeToLive(10, TimeUnit.MILLISECONDS).build();
        HttpGet get = new HttpGet(uri);
        try {
            HttpResponse resonse = client.execute(get);
            if (resonse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = resonse.getEntity();
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("download img error for url:" + uri);
        } finally {
            try {
                client.close();
            } catch (Exception e1) {
                LOGGER.warn("close client error");
            }
        }
        return new byte[0];
    }
}
