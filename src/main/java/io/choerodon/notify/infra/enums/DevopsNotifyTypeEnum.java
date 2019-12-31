package io.choerodon.notify.infra.enums;

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
    APP_SERVICE_CREATION_FAILURE("appServiceCreationFailure"),
    INGRESS_FAILURE("ingressFailure"),
    INSTANCE_FAILURE("instanceFailure"),
    CERTIFICATION_FAILURE("certificationFailure"),
    GITLAB_CONTINUOUS_DELIVERY_FAILURE("gitLabContinuousDeliveryFailure"),
    DISABLE_APP_SERVICE("disableAppService"),
    MERGE_REQUEST_CLOSED("mergeRequestClosed"),
    PIPELINE_SUCCESS("pipelinesuccess"),
    SERVICE_FAILURE("serviceFailure"),
    PIPELINE_FAILED("pipelinefailed"),
    ENABLE_APP_SERVICE("enableAppService"),
    MERGE_REQUEST_PASSED("mergeRequestPassed"),
    AUDIT_MERGE_REQUEST("auditMergeRequest");

    private final String value;

    DevopsNotifyTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static Map<String, Integer> orderMapping = new HashMap(13);

    static {
        orderMapping.put(APP_SERVICE_CREATION_FAILURE.value(), 1);
        orderMapping.put(ENABLE_APP_SERVICE.value(), 2);
        orderMapping.put(DISABLE_APP_SERVICE.value(), 3);
        orderMapping.put(GITLAB_CONTINUOUS_DELIVERY_FAILURE.value(), 4);
        orderMapping.put(MERGE_REQUEST_CLOSED.value(), 5);
        orderMapping.put(MERGE_REQUEST_PASSED.value(), 6);
        orderMapping.put(INSTANCE_FAILURE.value(), 7);
        orderMapping.put(SERVICE_FAILURE.value(), 8);
        orderMapping.put(INGRESS_FAILURE.value(), 9);
        orderMapping.put(CERTIFICATION_FAILURE.value(), 10);
        orderMapping.put(PIPELINE_SUCCESS.value(), 11);
        orderMapping.put(PIPELINE_FAILED.value(), 12);
        orderMapping.put(AUDIT_MERGE_REQUEST.value(), 13);
    }
}
