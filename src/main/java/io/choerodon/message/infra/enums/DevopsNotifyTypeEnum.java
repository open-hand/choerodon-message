package io.choerodon.message.infra.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈功能简述〉
 * 〈devops消息枚举〉
 *
 * @author wanghao
 * @Date 2019/12/17 20:41
 */
public enum DevopsNotifyTypeEnum {
    APP_SERVICE_CREATION_FAILURE("APPSERVICECREATIONFAILURE"),
    INGRESS_FAILURE("INGRESSFAILURE"),
    INSTANCE_FAILURE("INSTANCEFAILURE"),
    CERTIFICATION_FAILURE("CERTIFICATIONFAILURE"),
    GITLAB_CONTINUOUS_DELIVERY_FAILURE("GITLABCONTINUOUSDELIVERYFAILURE"),
    DISABLE_APP_SERVICE("DISABLEAPPSERVICE"),
    MERGE_REQUEST_CLOSED("MERGEREQUESTCLOSED"),
    PIPELINE_SUCCESS("PIPELINESUCCESS"),
    SERVICE_FAILURE("SERVICEFAILURE"),
    PIPELINE_FAILED("PIPELINEFAILED"),
    ENABLE_APP_SERVICE("ENABLEAPPSERVICE"),
    MERGE_REQUEST_PASSED("MERGEREQUESTPASSED"),
    AUDIT_MERGE_REQUEST("AUDITMERGEREQUEST");

    private final String value;

    DevopsNotifyTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static Map<String, Integer> orderMapping = new HashMap(13);

    static {
        orderMapping.put(APP_SERVICE_CREATION_FAILURE.value(), 10);
        orderMapping.put(ENABLE_APP_SERVICE.value(), 20);
        orderMapping.put(DISABLE_APP_SERVICE.value(), 30);
        orderMapping.put(GITLAB_CONTINUOUS_DELIVERY_FAILURE.value(), 40);
        orderMapping.put(MERGE_REQUEST_CLOSED.value(), 50);
        orderMapping.put(MERGE_REQUEST_PASSED.value(), 60);
        orderMapping.put(INSTANCE_FAILURE.value(), 70);
        orderMapping.put(SERVICE_FAILURE.value(), 80);
        orderMapping.put(INGRESS_FAILURE.value(), 90);
        orderMapping.put(CERTIFICATION_FAILURE.value(), 100);
        orderMapping.put(PIPELINE_SUCCESS.value(), 110);
        orderMapping.put(PIPELINE_FAILED.value(), 120);
        orderMapping.put(AUDIT_MERGE_REQUEST.value(), 130);
    }
}
