package io.choerodon.message.infra.utils;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
public class ConversionUtil {

    public static Integer booleanConverToInteger(Boolean index) {
        return index == null || !index ? 0 : 1;
    }

    public static Boolean IntegerConverToBoolean(Integer num) {
        return num != null && num != 0;
    }
}
