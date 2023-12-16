package com.lewigh.xsjvm.classloader.reader.info.attribute;

public record SignatureAttribute(
        String attributeName,
        int attributeLength,
        short signatureIndex
) implements AttributeInfo {
}
