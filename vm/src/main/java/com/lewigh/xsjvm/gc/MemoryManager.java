package com.lewigh.xsjvm.gc;

import com.lewigh.xsjvm.MemoryManagmentException;
import com.lewigh.xsjvm.engine.runtime.Field;
import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.Value;

import java.util.Collection;

public interface MemoryManager {

    long allocateObject(int classId, Collection<Field> fields, long payloadSize) throws MemoryManagmentException;

    long allocateArray(Jtype.Primitive type, int size) throws MemoryManagmentException;

    void setArrayElement(long address, int index, Jtype.Primitive type, Number value) throws MemoryManagmentException;

    Value getArrayElement(long address, int index, Jtype.Primitive type) throws MemoryManagmentException;

    int arrayLength(long address);

    void putWithType(long address, Jtype.Primitive type, Number value) throws MemoryManagmentException;

    void initWithType(long address, Jtype.Primitive type) throws MemoryManagmentException;

    Value getWithType(long address, Jtype.Primitive type) throws MemoryManagmentException;

    int getClassId(long objectAddress) throws MemoryManagmentException;

}
