package com.lewigh.xsjvm.engine.runtime;

import com.lewigh.xsjvm.classloader.reader.info.attribute.ExceptionTable;
import com.lewigh.xsjvm.classloader.reader.info.attribute.Instruction;

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
}
