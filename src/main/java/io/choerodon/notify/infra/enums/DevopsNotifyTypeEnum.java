package io.choerodon.notify.infra.enums;

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
}
