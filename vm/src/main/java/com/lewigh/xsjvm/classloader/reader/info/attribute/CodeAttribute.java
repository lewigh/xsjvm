package com.lewigh.xsjvm.classloader.reader.info.attribute;


public record CodeAttribute(
        String attributeName,
        int attributeLength,
        short maxStack,
        short maxLocals,
        int codeLength,
        Instruction[] code,
        short exceptionTableLength,
        ExceptionTable[] exceptionTable,
        short attributesCount,
        AttributeInfo[] attributes

) implements AttributeInfo {
}
