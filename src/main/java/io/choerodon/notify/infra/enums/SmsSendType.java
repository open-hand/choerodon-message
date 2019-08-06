package io.choerodon.notify.infra.enums;

/**
 * 短信发送类型
 *
 * @author superlee
 * @since 2019-05-19
 */
public enum SmsSendType {

    /**
     * 单条发送给用户
     */
    SINGLE("single"),

    /**
     * 批量发送给用户
     */
    BATCH("batch"),
    /**
     * 异步发送给用户
     */
    ASYNC("async");

    private String value;

    SmsSendType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static boolean isSingle(String value) {
        return SINGLE.value.equals(value);
    }

    public static boolean isBatch(String value) {
        return BATCH.value.equals(value);
    }

    public static boolean isAsync(String value) {
        return ASYNC.value.equals(value);
    }

    public static SmsSendType get(String value) {
        if (isSingle(value)) {
            return SINGLE;
        }
        if (isBatch(value)) {
            return BATCH;
        }
        if (isAsync(value)) {
            return ASYNC;
        }
        return null;
    }

    public static boolean contains(String value) {
        for (SmsSendType smsSendType : SmsSendType.values()) {
            if (smsSendType.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
