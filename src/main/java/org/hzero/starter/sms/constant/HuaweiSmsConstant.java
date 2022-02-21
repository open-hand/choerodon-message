package org.hzero.starter.sms.constant;

/**
 * 华为云短信常量
 *
 * @author dehui.ren@hand-china.com 2021/12/17 11:14
 */
public class HuaweiSmsConstant {

    public static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";
    public static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";

    public static class HuaweiParams {
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String TEMPLATE_ID= "templateId";
        public static final String TEMPLATE_PARAS = "templateParas";
        public static final String STATUS_CALL_BACK = "statusCallback";
        public static final String SIGNATURE = "signature";
        public static final String SENDER = "sender";
        public static final String POST = "POST";
        public static final String CONTENT_TYPE = "contentType";
        public static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
        public static final String AUTHORIZATION = "Authorization";
        public static final String X_WSSE = "X-WSSE";
        public static final String SSL = "SSL";
        public static final String SHA_256 = "SHA-256";

    }

}
