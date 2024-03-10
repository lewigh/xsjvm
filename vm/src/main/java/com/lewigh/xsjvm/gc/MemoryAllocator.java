package com.lewigh.xsjvm.gc;


public interface MemoryAllocator {

    byte getByte(long address);

    void putByte(long address, byte x);

    short getShort(long address);

    void putShort(long address, short x);

    char getChar(long address);

    void putChar(long address, char x);

    int getInt(long address);

    void putInt(long address, int x);

    long getLong(long address);

    void putLong(long address, long x);

    float getFloat(long address);

    void putFloat(long address, float x);

    double getDouble(long address);

    void putDouble(long address, double x);

    long getAddress(long address);

    void putAddress(long address, long x);

    long allocateMemory(long bytes);
}
