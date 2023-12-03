package com.lewigh.xsjvm.reader.info.attribute;


public record LocalVariableTableAttribute(
        String attributeName,
        int attributeLength,
        short localVariableTableLength,
        LocalVariableTableElement[] localVariableTable
) implements AttributeInfo {


    public record LocalVariableTableElement(
            short startPc,
            short length,
            short nameIndex,
            short descriptorIndex,
            short index
    ) {
    }
}
