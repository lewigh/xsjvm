package com.lewigh.xsjvm.reader.info.attribute;

public record SignatureAttribute(
        String attributeName,
        int attributeLength,
        short signatureIndex
) implements AttributeInfo {
}
