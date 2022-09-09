package org.hzero.starter.sms.support;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.net.ssl.*;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.sms.constant.HuaweiSmsConstant;
import org.hzero.starter.sms.exception.SendMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.exception.CommonException;


/**
 * 华为云短信发送支持
 *
 * @author dehui.ren@hand-china.com 2021/12/17 11:15
 */
public class HuaweiSmsSupporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuaweiSmsSupporter.class);

    public static void sendSms(String sender, String receiver, String templateId, String content, String templateParas, String statusCallBack, String signature, String appKey, String appSecret, String url) throws Exception {

        String body = buildRequestBody(sender, receiver, templateId, content, templateParas, statusCallBack, signature);
        if (body.isEmpty()) {
            throw new SendMessageException("body is null.");
        }

        String wsseHeader = buildWsseHeader(appKey, appSecret);
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            throw new SendMessageException("wsse header is null.");
        }

        Writer out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        HttpsURLConnection connection;
        InputStream is = null;


        HostnameVerifier hv = (hostname, session) -> true;
        trustAllHttpsCertificates();

        try {
            URL realUrl = new URL(url);
            connection = (HttpsURLConnection) realUrl.openConnection();
            connection.setHostnameVerifier(hv);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(true);
            connection.setRequestMethod(HuaweiSmsConstant.HuaweiParams.POST);
            connection.setRequestProperty(HuaweiSmsConstant.HuaweiParams.CONTENT_TYPE, HuaweiSmsConstant.HuaweiParams.CONTENT_TYPE_VALUE);
            connection.setRequestProperty(HuaweiSmsConstant.HuaweiParams.AUTHORIZATION, HuaweiSmsConstant.AUTH_HEADER_VALUE);
            connection.setRequestProperty(HuaweiSmsConstant.HuaweiParams.X_WSSE, wsseHeader);
            connection.connect();
            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(body);
            out.flush();
            out.close();

            int status = connection.getResponseCode();
            int responseCode = 200;
            if (responseCode != status) {
                is = connection.getErrorStream();
                in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line = "";
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                throw new SendMessageException("Failed to send message, response code : " + result);
            }
        } catch (Exception e) {
            throw new CommonException(e);
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != is) {
                    is.close();
                }
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
                LOGGER.error("exception: ", e);
            }
        }
    }


    /**
     * 构造请求Body体
     *
     * @param sender         发送人
     * @param receiver       接收人
     * @param templateId     模板id
     * @param templateParas  模板内容
     * @param statusCallBack 短信状态报告接收地址
     * @param signature      签名名称
     * @return 请求体
     */
    public static String buildRequestBody(String sender, String receiver, String templateId, String content, String templateParas,
                                          String statusCallBack, String signature) {
        if (null == sender || null == receiver || sender.isEmpty() || receiver.isEmpty()) {
            throw new SendMessageException("buildRequestBody(): sender, receiver or templateId is null.");
        }
        Map<String, String> map = new HashMap<>(8);

        map.put(HuaweiSmsConstant.HuaweiParams.FROM, sender);
        map.put(HuaweiSmsConstant.HuaweiParams.TO, receiver);
        if (!ObjectUtils.isEmpty(templateId)) {
            map.put(HuaweiSmsConstant.HuaweiParams.TEMPLATE_ID, templateId);
        } else {
            map.put(HuaweiSmsConstant.HuaweiParams.BODY, content);
        }
        if (null != templateParas && !templateParas.isEmpty()) {
            map.put(HuaweiSmsConstant.HuaweiParams.TEMPLATE_PARAS, templateParas);
        }
        if (null != statusCallBack && !statusCallBack.isEmpty()) {
            map.put(HuaweiSmsConstant.HuaweiParams.STATUS_CALL_BACK, statusCallBack);
        }
        if (null != signature && !signature.isEmpty()) {
            map.put(HuaweiSmsConstant.HuaweiParams.SIGNATURE, signature);
        }

        StringBuilder sb = new StringBuilder();
        String temp;

        for (String s : map.keySet()) {
            try {
                temp = URLEncoder.encode(map.get(s), BaseConstants.DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new CommonException(e);
            }
            sb.append(s).append(BaseConstants.Symbol.EQUAL).append(temp).append(BaseConstants.Symbol.AND);
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }


    /**
     * 构造X-WSSE参数值
     *
     * @param appKey    APP_Key
     * @param appSecret APP_Secret
     * @return X-WSSE参数值
     */
    public static String buildWsseHeader(String appKey, String appSecret) {
        if (null == appKey || null == appSecret || appKey.isEmpty() || appSecret.isEmpty()) {
            throw new SendMessageException("buildWsseHeader(): appKey or appSecret is null.");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String time = sdf.format(new Date());
        String nonce = UUID.randomUUID().toString().replace("-", "");

        MessageDigest md;
        byte[] passwordDigest = null;

        try {
            md = MessageDigest.getInstance(HuaweiSmsConstant.HuaweiParams.SHA_256);
            md.update((nonce + time + appSecret).getBytes());
            passwordDigest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new CommonException(e);
        }
        String passwordDigestBase64Str = Base64.getEncoder().encodeToString(passwordDigest);
        return String.format(HuaweiSmsConstant.WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }


    public static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return;
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance(HuaweiSmsConstant.HuaweiParams.SSL);
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}