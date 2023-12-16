package com.lewigh.xsjvm.classloader.reader.info.attribute;

public sealed interface ElementValue {

    TypeTag tag();

    record ConstValue(TypeTag tag, short constValueIndex) implements ElementValue {

    }

    record EnumConstValue(TypeTag tag, short typeNameIndex, short constNameIndex) implements ElementValue {

    }

    record ClassInfo(TypeTag tag, short classInfoIndex) implements ElementValue {

    }

    record Annotation(TypeTag tag, RuntimeVisibleAnnotationsAttribute.Annotation annotation) implements ElementValue {

    }

    record ArrayValue(TypeTag tag, short numValues, ElementValue[] values) implements ElementValue {

    }
}
