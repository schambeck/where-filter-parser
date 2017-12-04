package com.schambeck.wherefilter.parser.utils;

import org.parboiled.Node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class NodeUtils {

    private NodeUtils() {
    }

    public static String getValueExpression(Node node, String where) {
        return where.substring(node.getStartIndex(), node.getEndIndex());
    }

    public static <T> List<Object> getValues(String attributePathStr, Node itemValues, String where, Class<T> tClass) {
        Node sequenceNode = (Node) itemValues.getChildren().get(0);
        Node firstValueNode = (Node) sequenceNode.getChildren().get(0);
        Node otherValuesNode = (Node) sequenceNode.getChildren().get(1);
        List<Object> values = new ArrayList<>();

        String valueExpression = getValueExpression(firstValueNode, where).trim();
        Object value = getValue(attributePathStr, valueExpression, tClass);
        values.add(value);

        for (Object objectValue : otherValuesNode.getChildren()) {
            sequenceNode = (Node) objectValue;
            Node valueNode = (Node) sequenceNode.getChildren().get(1);
            valueExpression = getValueExpression(valueNode, where);
            value = getValue(attributePathStr, valueExpression, tClass);
            values.add(value);
        }
        return values;
    }

    public static <T> Object getValue(String attributePath, String value, Class<T> tClass) {
//        Field field = tClass.getDeclaredField(attributePath);
//        Class<?> type = field.getType();
        Class<?> type = ReflectionUtils.getPropertyType(tClass, attributePath);
        if (type.isAssignableFrom(String.class)) {
            return getStringValue(value);
        }
        if (type.isAssignableFrom(Long.class)) {
            return Long.parseLong(value);
        }
        if (type.isAssignableFrom(Integer.class)) {
            return Integer.parseInt(value);
        }
        if (type.isAssignableFrom(Float.class)) {
            return Float.parseFloat(value);
        }
        if (type.isAssignableFrom(Double.class)) {
            return Double.parseDouble(value);
        }
        if (type.isAssignableFrom(BigDecimal.class)) {
            return new BigDecimal(value);
        }
        if (type.isAssignableFrom(LocalDate.class)) {
            return LocalDate.parse(getStringValue(value));
        }
        if (type.isEnum()) {
            Class<Enum> enumType = (Class<Enum>) type;
            Enum enumValue = Enum.valueOf(enumType, value);
            return enumValue;
        }
        return null;
    }

    private static String getStringValue(String value) {
        int beginIndex = value.indexOf("'");
        int endIndex = value.lastIndexOf("'");
        if (beginIndex == 0 && endIndex == value.length() - 1) {
            return value.substring(++beginIndex, endIndex);
        } else {
            return value;
        }
    }

}
