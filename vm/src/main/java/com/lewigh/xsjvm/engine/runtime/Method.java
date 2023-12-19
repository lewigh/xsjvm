package com.lewigh.xsjvm.engine.runtime;

import com.lewigh.xsjvm.classloader.reader.info.attribute.ExceptionTable;
import com.lewigh.xsjvm.classloader.reader.info.attribute.Instruction;

import java.util.Arrays;
import java.util.Objects;

public record Method(
        String name,
        MethodDescriptor descriptor,
        Access access,
        boolean fStatic,
        boolean fFinal,
        boolean fNative,
        boolean fSynchronized,
        boolean fAbstract,
        boolean fVarargs,
        boolean fBridge,
        boolean fSyntetic,
        boolean fStrict,
        short maxStack,
        short maxLocals,
        Instruction[] instructions,
        ExceptionTable[] exceptionTable
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return fStatic == method.fStatic && fFinal == method.fFinal && fNative == method.fNative && fSynchronized == method.fSynchronized && fAbstract == method.fAbstract && fVarargs == method.fVarargs && fBridge == method.fBridge && fSyntetic == method.fSyntetic && fStrict == method.fStrict && maxStack == method.maxStack && maxLocals == method.maxLocals && Objects.equals(name, method.name) && Objects.equals(descriptor, method.descriptor) && access == method.access && Arrays.equals(instructions, method.instructions) && Arrays.equals(exceptionTable, method.exceptionTable);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, descriptor, access, fStatic, fFinal, fNative, fSynchronized, fAbstract, fVarargs, fBridge, fSyntetic, fStrict, maxStack, maxLocals);
        result = 31 * result + Arrays.hashCode(instructions);
        result = 31 * result + Arrays.hashCode(exceptionTable);
        return result;
    }

    @Override
    public String toString() {
        return "Method{" +
                "name='" + name + '\'' +
                ", descriptor=" + descriptor +
                ", access=" + access +
                ", fStatic=" + fStatic +
                ", fFinal=" + fFinal +
                ", fNative=" + fNative +
                ", fSynchronized=" + fSynchronized +
                ", fAbstract=" + fAbstract +
                ", fVarargs=" + fVarargs +
                ", fBridge=" + fBridge +
                ", fSyntetic=" + fSyntetic +
                ", fStrict=" + fStrict +
                ", maxStack=" + maxStack +
                ", maxLocals=" + maxLocals +
                ", instructions=" + Arrays.toString(instructions) +
                ", exceptionTable=" + Arrays.toString(exceptionTable) +
                '}';
    }
}
