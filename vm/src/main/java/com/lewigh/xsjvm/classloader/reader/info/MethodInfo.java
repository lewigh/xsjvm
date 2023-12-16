package com.lewigh.xsjvm.classloader.reader.info;


import com.lewigh.xsjvm.classloader.reader.flag.MethodAccessFlag;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;

public record MethodInfo(short accessFlags,
                         String name,
                         String descriptor,
                         int attributesCount,
                         AttributeInfo[] attributes) {

    public boolean isPublic() {
        return hasFlag(MethodAccessFlag.ACC_PUBLIC);
    }

    public boolean isPrivate() {
        return hasFlag(MethodAccessFlag.ACC_PRIVATE);
    }

    public boolean isProtected() {
        return hasFlag(MethodAccessFlag.ACC_PROTECTED);
    }

    public boolean isStatic() {
        return hasFlag(MethodAccessFlag.ACC_STATIC);
    }

    public boolean isFinal() {
        return hasFlag(MethodAccessFlag.ACC_FINAL);
    }

    public boolean isSynchronized() {
        return hasFlag(MethodAccessFlag.ACC_SYNCHRONIZED);
    }

    public boolean isBridge() {
        return hasFlag(MethodAccessFlag.ACC_BRIDGE);
    }

    public boolean isVarargs() {
        return hasFlag(MethodAccessFlag.ACC_BRIDGE);
    }

    public boolean isStrict() {
        return hasFlag(MethodAccessFlag.ACC_STRICT);
    }

    public boolean isNative() {
        return hasFlag(MethodAccessFlag.ACC_NATIVE);
    }

    public boolean isAbstract() {
        return hasFlag(MethodAccessFlag.ACC_ABSTRACT);
    }

    public boolean isSynthetic() {
        return hasFlag(MethodAccessFlag.ACC_SYNTHETIC);
    }


    private boolean hasFlag(MethodAccessFlag flag) {
        int hex = flag.getHex();
        return (accessFlags & hex) == hex;
    }
}
