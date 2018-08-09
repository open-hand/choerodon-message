package io.choerodon.notify.infra.utils;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;

public class ConvertUtils {

    private ConvertUtils() {
    }

    public static Converter<String, String> addPrefix(String prefix) {
        return new AbstractConverter<String, String>() {
            @Override
            protected String convert(String source) {
                return prefix + toUpperCaseFirstOne(source);
            }
        };
    }

    public static Converter<String, String> removePrefix(String prefix) {
        return new AbstractConverter<String, String>() {
            @Override
            protected String convert(String source) {
                return toLowerCaseFirstOne(source.replaceFirst(prefix, ""));
            }
        };
    }


    private static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    private static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
