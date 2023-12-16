package com.lewigh.xsjvm.classloader.reader.info.attribute;


public record StackMapTableAttribute(
        String attributeName,
        int attributeLength,
        short numberOfEntrie
) implements AttributeInfo {
}
