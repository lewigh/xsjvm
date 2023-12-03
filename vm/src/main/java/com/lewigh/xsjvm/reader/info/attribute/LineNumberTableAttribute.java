package com.lewigh.xsjvm.reader.info.attribute;


public record LineNumberTableAttribute(
        String attributeName,
        int attributeLength,
        short lineNumberTableLength,
        LineNumberTable[] lineNumberTable
) implements AttributeInfo {


    public record LineNumberTable(short startPc,
                                  short lineNumber) {
    }
}
