package com.lewigh.xsjvm.reader.info.attribute;

public record DeprecatedAttribute(
        String attributeName,
        int attributeLength
) implements AttributeInfo {
}
