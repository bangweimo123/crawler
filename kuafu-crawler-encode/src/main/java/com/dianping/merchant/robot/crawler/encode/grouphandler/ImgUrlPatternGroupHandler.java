package com.dianping.merchant.robot.crawler.encode.grouphandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
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
import com.dianping.piccenter.upload.api.HttpUploadAPI;
import com.dianping.piccenter.upload.api.TokenBuilder;
import com.dianping.piccenter.upload.api.UploadPicFlowType;
import com.dianping.piccenter.upload.api.UploadPicToken;
import com.dianping.piccentercloud.display.api.PictureUrlGenerator;
import com.dianping.piccentercloud.display.api.PictureVisitParams;
import com.dianping.piccentercloud.display.api.enums.PictureVisitPattern;
import com.dianping.piccentercloud.display.api.enums.WaterMark;

/**
 * 替换图片
 * 
 * @author mobangwei
 * 
 */
public class ImgUrlPatternGroupHandler extends SimplePatternGroupHandler {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(ImgUrlPatternGroupHandler.class);


    @Override
    public String doReplacer(Integer group, String data) {
        String returnData = StringUtils.EMPTY;
        switch (group) {
            case 2:
                returnData = doHandlerImg(data);
                break;
            case 4:
                returnData = doHandlerParam(data);
                break;
        }
        return returnData;
    }

    private String doHandlerImg(String data) {
        String returnData = StringUtils.EMPTY;
        if (isLegalUrl(data)) {
            byte[] imgByte = downloadImg(data);
            returnData = upload(imgByte, data);
            returnData = picUrlRewrite(returnData);
        }
        return returnData;
    }

    private String doHandlerParam(String data) {
        return StringUtils.EMPTY;
    }

    private boolean isLegalUrl(String url) {
        return url.matches("http://([^']+(?:jpg|gif|png|bmp|jpeg))(\\S+)?");
    }

    private static String picUrlRewrite(String key) {
        PictureVisitParams params = new PictureVisitParams(UplaodConstant.UPLOAD_BIZ, key, UplaodConstant.SCALE, UplaodConstant.CROP, 0, 0, WaterMark.DIANPING);
        PictureUrlGenerator pictureUrlGenerator = new PictureUrlGenerator(params, PictureVisitPattern.COMPATIBLE);
        String finalPictureUrl = pictureUrlGenerator.getFullPictureURL();
        return finalPictureUrl;
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
        public static final String UPLOAD_BIZ = "pc";
        public static final Integer UPLOAD_READ_TIMEOUT = 10000;
        public static final Integer UPLOAD_CONNECT_TIMEOUT = 6000;
        public static final String UPLOAD_SECRENT_KEY = "713549987bfb175c96606ebf4d99845c";

        public static final Integer SCALE = 0;
        public static final Integer CROP = 0;
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
