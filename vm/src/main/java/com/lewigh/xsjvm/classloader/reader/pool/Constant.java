package com.lewigh.xsjvm.classloader.reader.pool;

public sealed interface Constant {

    record ConstantInteger(int value) implements Constant {
    }

    record ConstantFloat(float value) implements Constant {
    }

    record ConstantLong(long value) implements Constant {
    }

    record ConstantDouble(double value) implements Constant {
    }

    record ConstantString(short index) implements Constant {
    }

    record Utf8(String text) implements Constant {
    }

    record Class(short nameIndex) implements Constant {
    }

    record DynamicInfo(short bootstrapMethodAttrIndex, short nameAndTypeIndex) implements Constant {
    }

    record FieldInfo(short classIndex, short nameAndTypeIndex) implements Constant {
    }

    record InterfaceMethodRef(short classIndex, short nameAndTypeIndex) implements Constant {
    }

    record InvokeDynamicInfo(short bootstrapMethodAttrIndex, short nameAndTypeIndex) implements Constant {
    }

    record MethodHandleInfo(byte referenceKind, short referenceIndex) implements Constant {
    }

    record MethodRefInfo(short classIndex, short nameAndTypeIndex) implements Constant {
    }

    record MethodTypeInfo(short descriptorIndex) implements Constant {
    }

    record ModuleInfo(short nameIndex) implements Constant {
    }

    record NameAndTypeInfo(short nameIndex, short descriptorIndex) implements Constant {
    }

    record PackageInfo(short nameIndex) implements Constant {
    }
}
