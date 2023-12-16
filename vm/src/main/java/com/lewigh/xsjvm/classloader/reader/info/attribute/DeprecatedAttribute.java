package com.lewigh.xsjvm.classloader.reader.info.attribute;

public record DeprecatedAttribute(
        String attributeName,
        int attributeLength
) implements AttributeInfo {
}
