package com.lewigh.xsjvm.mem;

import com.lewigh.xsjvm.MemoryManagmentException;
import sun.misc.Unsafe;

public class UnsafeMemoryAllocator implements MemoryAllocator {

    private final Unsafe unsafe;

    private UnsafeMemoryAllocator(Unsafe unsafe) {
        this.unsafe = unsafe;
    }

    public static UnsafeMemoryAllocator create() throws MemoryManagmentException {
        try {
            java.lang.reflect.Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);

            return new UnsafeMemoryAllocator(unsafe);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MemoryManagmentException("Error while creating UB", e);
        }
    }

    @Override
    public byte getByte(long address) {
        return unsafe.getByte(address);
    }

    @Override
    public void putByte(long address, byte x) {
        unsafe.putByte(address, x);
    }

    @Override
    public short getShort(long address) {
        return unsafe.getShort(address);
    }

    @Override
    public void putShort(long address, short x) {
        unsafe.putShort(address, x);
    }

    @Override
    public char getChar(long address) {
        return unsafe.getChar(address);
    }

    @Override
    public void putChar(long address, char x) {
        unsafe.putChar(address, x);
    }

    @Override
    public int getInt(long address) {
        return unsafe.getInt(address);
    }

    @Override
    public void putInt(long address, int x) {
        unsafe.putInt(address, x);
    }

    @Override
    public long getLong(long address) {
        return unsafe.getLong(address);
    }

    @Override
    public void putLong(long address, long x) {
        unsafe.putLong(address, x);
    }

    @Override
    public float getFloat(long address) {
        return unsafe.getFloat(address);
    }

    @Override
    public void putFloat(long address, float x) {
        unsafe.putFloat(address, x);
    }

    @Override
    public double getDouble(long address) {
        return unsafe.getDouble(address);
    }

    @Override
    public void putDouble(long address, double x) {
        unsafe.putDouble(address, x);
    }

    @Override
    public long getAddress(long address) {
        return unsafe.getAddress(address);
    }

    @Override
    public void putAddress(long address, long x) {
        unsafe.putAddress(address, x);
    }

    @Override
    public long allocate(long bytes) {
        return unsafe.allocateMemory(bytes);
    }
}
