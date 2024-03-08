package com.lewigh.xsjvm.classloader.reader.pool;

import com.lewigh.xsjvm.engine.runtime.IntoValue;
import com.lewigh.xsjvm.engine.runtime.Value;

public sealed interface Constant {

    record ConstantInteger(int value) implements Constant, IntoValue {
        @Override
        public Value into() {
            return new Value.Int(value);
        }
    }

    record ConstantFloat(float value) implements Constant, IntoValue {
        @Override
        public Value into() {
            return new Value.Float(value);
        }
    }

    record ConstantLong(long value) implements Constant, IntoValue {
        @Override
        public Value into() {
            return new Value.Long(value);
        }
    }

    record ConstantDouble(double value) implements Constant, IntoValue {
        @Override
        public Value into() {
            return new Value.Double(value);
        }
    }

    record ConstantStringRef(short index) implements Constant, IntoValue {
        @Override
        public Value into() {
            return Value.Reference.from(index);
        }
    }

    record Utf8(String text) implements Constant {
    }

    record Class(short nameIndex) implements Constant, IntoValue {
        @Override
        public Value into() {
            return Value.Reference.from(nameIndex);
        }
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
