package com.lewigh.xsjvm.classloader.reader.info.attribute;

public sealed interface AttributeInfo permits
        CodeAttribute,
        LineNumberTableAttribute,
        LocalVariableTableAttribute,
        LocalVariableTypeTableAttribute,
        RuntimeVisibleAnnotationsAttribute,
        StackMapTableAttribute,
        SignatureAttribute,
        ExceptionsAttribute,
        DeprecatedAttribute,
        MethodParametersAttribute,
        ConstantValueAttribute {

    String attributeName();

    int attributeLength();

}
