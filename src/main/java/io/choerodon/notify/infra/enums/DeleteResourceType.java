package io.choerodon.notify.infra.enums;

import io.choerodon.core.notify.TargetUserType;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mr.Wang
 * Date: 2019/12/11
 */
public enum DeleteResourceType {
    INSTANCE("instance"),
    INGRESS("ingress"),
    CONFIGMAP("configMap"),
    CERTIFICATE("certificate"),
    SECRET("secret"),
    /**
     * 删除网络
     */
    SERVICE("service");

    private final String typeName;

    DeleteResourceType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static final Map<String, String> nameMapping = new HashMap(6);

    static {
        nameMapping.put(INSTANCE.getTypeName(), "删除实例");
        nameMapping.put(INGRESS.getTypeName(), "删除域名");
        nameMapping.put(SERVICE.getTypeName(), "删除网络");
        nameMapping.put(CONFIGMAP.getTypeName(), "删除配置映射");
        nameMapping.put(CERTIFICATE.getTypeName(), "删除证书");
        nameMapping.put(SECRET.getTypeName(), "删除密文");
    }

    public static Map<String, String> notifyTargetMapping = new HashMap(6);

    static {
        notifyTargetMapping.put(INSTANCE.getTypeName(), TargetUserType.PROJECT_OWNER.getTypeName());
        notifyTargetMapping.put(INGRESS.getTypeName(), TargetUserType.HANDLER.getTypeName());
        notifyTargetMapping.put(SERVICE.getTypeName(), TargetUserType.HANDLER.getTypeName());
        notifyTargetMapping.put(CONFIGMAP.getTypeName(), TargetUserType.HANDLER.getTypeName());
        notifyTargetMapping.put(CERTIFICATE.getTypeName(), TargetUserType.PROJECT_OWNER.getTypeName());
        notifyTargetMapping.put(SECRET.getTypeName(), TargetUserType.HANDLER.getTypeName());
    }

    public static Map<String, Integer> orderMapping = new HashMap(6);

    static {
        orderMapping.put(INSTANCE.getTypeName(), 10);
        orderMapping.put(INGRESS.getTypeName(), 20);
        orderMapping.put(SERVICE.getTypeName(), 30);
        orderMapping.put(CONFIGMAP.getTypeName(), 40);
        orderMapping.put(CERTIFICATE.getTypeName(), 50);
        orderMapping.put(SECRET.getTypeName(), 60);
    }
}
