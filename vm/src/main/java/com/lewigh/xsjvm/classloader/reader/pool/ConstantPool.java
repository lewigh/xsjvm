package com.lewigh.xsjvm.classloader.reader.pool;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class ConstantPool {
    private final Map<Short, Constant> constants = new HashMap<>();

    public Constant get(short index) {
        return Objects.requireNonNull(constants.get(index), "Invalid index %d of the constant pool".formatted(index));
    }

    public String resolveUtf8Ref(short index) {
        Constant.Utf8 utf8 = (Constant.Utf8) get(index);
        return utf8.text();
    }

    public String resolveClassRef(short classIndex) {
        var stringIndex = (Constant.Class) get(classIndex);
        return resolveUtf8Ref(stringIndex.nameIndex());
    }

    public Constant.MethodRefInfo resolveMethorRefInfo(short index) {
        return (Constant.MethodRefInfo) get(index);
    }

    public Constant.FieldInfo resolveFieldInfo(short index) {
        return (Constant.FieldInfo) get(index);
    }

    public Constant.NameAndTypeInfo resolveNameAndTypeInfo(short index) {
        return (Constant.NameAndTypeInfo) get(index);
    }

    public void add(short index, Constant object) {
        constants.put(index, object);
    }

    public short check(short index) {
        get(index);
        return index;
    }

    public short checkOrZero(short index) {
        if (index == 0) {
            return 0;
        }
        get(index);
        return index;
    }
}
