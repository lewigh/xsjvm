package com.lewigh.xsjvm.engine.runtime;

import com.lewigh.xsjvm.classloader.reader.flag.ClassAccessFlag;
import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.engine.InvokeType;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.lewigh.xsjvm.SymbolTable.CLINIT_METH_FULL_NAME;
import static com.lewigh.xsjvm.classloader.reader.flag.ClassAccessFlag.ACC_INTERFACE;

public record KlassDesc(
        int id,
        String name,
        KlassDesc superKlass,
        ClassAccessFlag[] accessFlags,
        KlassDesc[] interfaces,
        FieldDescGroup fieldGroup,
        Map<String, MethodDesc> methods,
        Map<String, MethodDesc> vtable,
        ConstantPool constantPool,
        State state
) {

    public static class State {
        private boolean init;
        private long staticAddress;
    }


    public boolean isInit() {
        return state.init;
    }

    public void setInit(boolean n) {
        state.init = n;
    }

    public void setStaticAddress(long address) {
        state.staticAddress = address;
    }

    public long staticAddress() {
        return state.staticAddress;
    }

    public MethodDesc getClinit() {
        return methods().get(CLINIT_METH_FULL_NAME);
    }

    public MethodDesc findMethod(String name, String descriptor, InvokeType invokeType) {
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

    private void checkAccess(String name, MethodDesc methodMeta, InvokeType invokeType) {
        if (methodMeta == null) {
            throw new IllegalArgumentException("Method %s is not found".formatted(name));
        }
//        if (invokeType == InvokeType.STATIC && !methodMeta.fStatic()) {
//            throw new IllegalArgumentException("Method %s is not found".formatted(name));
//        }
    }

    public boolean isInterface() {
        return Stream.of(this.accessFlags)
                .anyMatch(af -> af == ACC_INTERFACE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KlassDesc klass = (KlassDesc) o;
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
