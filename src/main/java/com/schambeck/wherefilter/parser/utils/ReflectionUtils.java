package com.schambeck.wherefilter.parser.utils;

import org.parboiled.common.StringUtils;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    static Class<?> getPropertyType(Class<?> clazz, String fieldName) {
        final String[] fieldNames = fieldName.split("\\.", -1);
        if (fieldNames.length > 1) {
            final String firstProperty = fieldNames[0];
            final String otherProperties =
                    StringUtils.join(fieldNames, ".", 1, fieldNames.length);
            final Class<?> firstPropertyType = getPropertyType(clazz, firstProperty);
            return getPropertyType(firstPropertyType, otherProperties);
        }

        try {
            return clazz.getDeclaredField(fieldName).getType();
        } catch (final NoSuchFieldException e) {
            if (!clazz.equals(Object.class)) {
                return getPropertyType(clazz.getSuperclass(), fieldName);
            }
            throw new IllegalStateException(e);
        }
    }

}
