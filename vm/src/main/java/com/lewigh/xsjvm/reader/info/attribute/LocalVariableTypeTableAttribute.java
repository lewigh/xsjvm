package com.lewigh.xsjvm.reader.info.attribute;


public record LocalVariableTypeTableAttribute(
        String attributeName,
        int attributeLength,
        short localVariableTypeTableLength,
        LocalVariableTypeTableElement[] localVariableTable
) implements AttributeInfo {

    public record  LocalVariableTypeTableElement(
            short startPc,
            short length,
            short nameIndex,
            short signatureIndex,
            short index
    ) {
    }
}
