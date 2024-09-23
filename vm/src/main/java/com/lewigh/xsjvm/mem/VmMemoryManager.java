package com.lewigh.xsjvm.mem;

import com.lewigh.xsjvm.MemoryManagmentException;
import com.lewigh.xsjvm.engine.runtime.FieldDesc;
import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.Value;

import java.util.Collection;

public interface VmMemoryManager {

    long allocateObject(int classId, Collection<FieldDesc> fields, long payloadSize) throws MemoryManagmentException;

    long allocateArray(Jtype.Primitive type, int size) throws MemoryManagmentException;

    long allocateArray(int classId, Collection<FieldDesc> fields, long payloadSize, int size);

    void setArrayElement(long address, int index, Jtype.Primitive type, Number value) throws MemoryManagmentException;

    Value getArrayElement(long address, int index, Jtype.Primitive type) throws MemoryManagmentException;

    int arrayLength(long address);

    void putWithType(long address, Jtype.Primitive type, Number value) throws MemoryManagmentException;

    void initWithType(long address, Jtype.Primitive type) throws MemoryManagmentException;

    Value getWithType(long address, Jtype.Primitive type) throws MemoryManagmentException;

    int getClassId(long objectAddress) throws MemoryManagmentException;

}
