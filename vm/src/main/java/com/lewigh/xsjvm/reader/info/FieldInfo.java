package com.lewigh.xsjvm.reader.info;


import com.lewigh.xsjvm.reader.flag.FiledAccessFlag;
import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;

import java.util.Arrays;
import java.util.Objects;

public record FieldInfo(short accessFlags,
                        String name,
                        String descriptor,
                        AttributeInfo[] attributes) {

    public boolean isPublic() {
        return hasFlag(FiledAccessFlag.ACC_PUBLIC);
    }

    public boolean isPrivate() {
        return hasFlag(FiledAccessFlag.ACC_PRIVATE);
    }

    public boolean isProtected() {
        return hasFlag(FiledAccessFlag.ACC_PROTECTED);
    }

    public boolean isStatic() {
        return hasFlag(FiledAccessFlag.ACC_STATIC);
    }

    public boolean isFinal() {
        return hasFlag(FiledAccessFlag.ACC_FINAL);
    }

    public boolean isVolatile() {
        return hasFlag(FiledAccessFlag.ACC_VOLATILE);
    }

    public boolean isTransient() {
        return hasFlag(FiledAccessFlag.ACC_TRANSIENT);
    }

    public boolean isSynthetic() {
        return hasFlag(FiledAccessFlag.ACC_SYNTHETIC);
    }

    public boolean isEnum() {
        return hasFlag(FiledAccessFlag.ACC_ENUM);
    }


    private boolean hasFlag(FiledAccessFlag flag) {
        int hex = flag.getHex();
        return (accessFlags & hex) == hex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldInfo fieldInfo = (FieldInfo) o;
        return accessFlags == fieldInfo.accessFlags && Objects.equals(name, fieldInfo.name) && Objects.equals(descriptor, fieldInfo.descriptor) && Arrays.equals(attributes, fieldInfo.attributes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(accessFlags, name, descriptor);
        result = 31 * result + Arrays.hashCode(attributes);
        return result;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "accessFlags=" + accessFlags +
                ", name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }
}
