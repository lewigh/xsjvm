package com.lewigh.xsjvm.interpreter.runtime;

import com.lewigh.xsjvm.interpreter.InvokeType;
import com.lewigh.xsjvm.reader.flag.ClassAccessFlag;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.Data;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static com.lewigh.xsjvm.SymbolTable.CLINIT_METH_FULL_NAME;

public record Klass(
        int id,
        String name,
        Klass superKlass,
        ClassAccessFlag[] accessFlags,
        Klass[] interfaces,
        FieldGroup fieldGroup,
        Map<String, Method> methods,
        Map<String, Method> vtable,
        ConstantPool constantPool,
        State state
) {

    @Data
    public static class State {
        private boolean init;
        private long staticAddress;
    }

    public Method getClinit() {
        return methods().get(CLINIT_METH_FULL_NAME);
    }

    public Method findMethod(String name, String descriptor, InvokeType invokeType) {
        var signature = "%s%s".formatted(name, descriptor);
        return switch (invokeType) {
            case STATIC -> {
                var methodMeta = methods.get(signature);
                checkAccess(name, methodMeta, invokeType);
                yield methodMeta;
            }
            case SPECIAL -> {
                var methodMeta = methods.get(signature);
                checkAccess(name, methodMeta, invokeType);
                yield methodMeta;
            }
            case VIRTUAL -> {
                var methodMeta = vtable.get(signature);
                checkAccess(name, methodMeta, invokeType);
                yield methodMeta;
            }
            case INTERFACE -> {
                yield null;
            }
        };
    }

    private void checkAccess(String name, Method methodMeta, InvokeType invokeType) {
        if (methodMeta == null) {
            throw new IllegalArgumentException("Method %s is not found".formatted(name));
        }
//        if (invokeType == InvokeType.STATIC && !methodMeta.fStatic()) {
//            throw new IllegalArgumentException("Method %s is not found".formatted(name));
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klass klass = (Klass) o;
        return Objects.equals(name, klass.name)
                && Objects.equals(superKlass, klass.superKlass)
                && Arrays.equals(accessFlags, klass.accessFlags)
                && Arrays.equals(interfaces, klass.interfaces)
                && Objects.equals(fieldGroup, klass.fieldGroup)
                && Objects.equals(methods, klass.methods)
                && Objects.equals(vtable, klass.vtable)
                && Objects.equals(constantPool, klass.constantPool);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, superKlass, fieldGroup, methods, vtable, constantPool);
        result = 31 * result + Arrays.hashCode(accessFlags);
        result = 31 * result + Arrays.hashCode(interfaces);
        return result;
    }

    @Override
    public String toString() {
        return "klass{" +
                "name='" + name + '\'' +
                ", superKlass=" + superKlass +
                ", accessFlags=" + Arrays.toString(accessFlags) +
                ", interfaces=" + Arrays.toString(interfaces) +
                ", fieldGroup=" + fieldGroup +
                ", methods=" + methods +
                ", vtable=" + vtable +
                ", constantPool=" + constantPool +
                '}';
    }
}
