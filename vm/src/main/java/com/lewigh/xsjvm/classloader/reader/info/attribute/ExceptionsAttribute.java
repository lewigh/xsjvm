package com.lewigh.xsjvm.classloader.reader.info.attribute;

public record ExceptionsAttribute(
        String attributeName,
        int attributeLength,
        short number_of_exceptions,
        short[] exception_index_table

) implements AttributeInfo {
}
