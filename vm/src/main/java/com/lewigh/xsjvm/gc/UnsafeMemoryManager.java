package com.lewigh.xsjvm.gc;

import com.lewigh.xsjvm.MemoryManagmentException;
import com.lewigh.xsjvm.interpreter.runtime.Field;
import com.lewigh.xsjvm.interpreter.runtime.Jtype;
import com.lewigh.xsjvm.interpreter.runtime.Value;
import sun.misc.Unsafe;

import java.util.Collection;

public class UnsafeMemoryManager {

    private static final int MARK_WORD_HEADER_SIZE = 4;
    private static final int CLASS_WORD_HEADER_SIZE = 4;
    private static final int ARRAY_HEADER_SIZE = 4;
    private static final int OBJECT_HEADERS_SIZE = MARK_WORD_HEADER_SIZE + CLASS_WORD_HEADER_SIZE;

    private final Unsafe unsafe;

    private UnsafeMemoryManager(Unsafe unsafe) {
        this.unsafe = unsafe;
    }

    public static UnsafeMemoryManager create() throws MemoryManagmentException {
        try {
            java.lang.reflect.Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);

            return new UnsafeMemoryManager(unsafe);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MemoryManagmentException("Error while creating UB", e);
        }
    }


    public long allocateObject(int classId, Collection<Field> fields, long payloadSize) throws MemoryManagmentException {
        try {
            long objectAddress = unsafe.allocateMemory(computeTotalObjectSize(payloadSize));

            long cursor = objectAddress;

            cursor += MARK_WORD_HEADER_SIZE;

            unsafe.putInt(cursor, classId);

            cursor += CLASS_WORD_HEADER_SIZE;

            for (var field : fields) {
                var type = field.type().primitive();

                initWithType(cursor, type);
                cursor += type.getAlign().getTotal();
            }

            return objectAddress;
        } catch (Throwable e) {
            throw new MemoryManagmentException("Error while allocationg an object", e);
        }

    }


    public long allocateArray(Jtype.Primitive type, int size) throws MemoryManagmentException {
        try {

            int payloadSize = type.getAlign().getTotal() * size;

            int totalSize = OBJECT_HEADERS_SIZE + payloadSize;

            long objectAddress = unsafe.allocateMemory(totalSize);

            long cursor = objectAddress;

            cursor += MARK_WORD_HEADER_SIZE;

            unsafe.putInt(cursor, 0);

            cursor += CLASS_WORD_HEADER_SIZE;

            unsafe.putInt(cursor, size);

            cursor += ARRAY_HEADER_SIZE;

            for (int i = 0; i < size; i++) {
                initWithType(cursor, type);
                cursor += type.getAlign().getTotal();
            }

            return objectAddress;
        } catch (Throwable e) {
            throw new MemoryManagmentException("", e);
        }
    }

    public void setArrayElement(long address, int index, Jtype.Primitive type, Number value) throws MemoryManagmentException {
        try {
            var cursor = address + OBJECT_HEADERS_SIZE + ARRAY_HEADER_SIZE;

            var off = type.getAlign().getTotal() * index;

            cursor += off;

            putWithType(cursor, type, value);
        } catch (Throwable e) {
            throw new MemoryManagmentException("Error while allocationg an array", e);
        }
    }

    public Value getArrayElement(long address, int index, Jtype.Primitive type) throws MemoryManagmentException {

        try {
            var offset = OBJECT_HEADERS_SIZE + ARRAY_HEADER_SIZE;
            var cursor = address + offset;

            var off = type.getAlign().getTotal() * index;

            cursor += off;

            return getWithType(cursor, type);
        } catch (Throwable e) {
            throw new MemoryManagmentException(e);
        }
    }

    public int arrayLength(long address) {
        var cur = address + OBJECT_HEADERS_SIZE;

        return getWithType(cur, Jtype.Primitive.INT).asInt();
    }


    public void putWithType(long address, Jtype.Primitive type, Number value) throws MemoryManagmentException {
        try {
            switch (type) {
                case VOID -> {
                }
                case BYTE, BOOL -> {
                    unsafe.putByte(address, (byte) value);
                }
                case SHORT -> unsafe.putShort(address, (short) value);
                case CHAR -> unsafe.putChar(address, (char) ((int) value));
                case INT -> unsafe.putInt(address, value.intValue());
                case LONG -> unsafe.putLong(address, (long) value);
                case FLOAT -> unsafe.putFloat(address, (float) value);
                case DOUBLE -> unsafe.putDouble(address, (double) value);
                case REFERENCE, ARRAY -> unsafe.putAddress(address, (long) value);
            }
        } catch (Throwable e) {
            throw new MemoryManagmentException("Unable to put value into memory", e);
        }
    }

    public void initWithType(long address, Jtype.Primitive type) throws MemoryManagmentException {
        try {


            switch (type) {
                case VOID -> {
                }
                case BYTE, BOOL -> unsafe.putByte(address, (byte) 0);
                case SHORT -> unsafe.putShort(address, (short) 0);
                case CHAR -> unsafe.putChar(address, (char) 0);
                case INT -> unsafe.putInt(address, 0);
                case LONG -> unsafe.putLong(address, 0L);
                case FLOAT -> unsafe.putFloat(address, 0.0f);
                case DOUBLE -> unsafe.putDouble(address, 0d);
                case REFERENCE, ARRAY -> unsafe.putAddress(address, 0L);
            }
        } catch (Throwable e) {
            throw new MemoryManagmentException(e);
        }
    }

    public Value getWithType(long address, Jtype.Primitive type) throws MemoryManagmentException {
        try {
            return switch (type) {
                case VOID -> {
                    throw new RuntimeException();
                }
                case BYTE -> new Value.Byte(unsafe.getByte(address));
                case BOOL -> new Value.Bool(unsafe.getByte(address) != 0);
                case SHORT -> new Value.Short(unsafe.getShort(address));
                case CHAR -> new Value.Char(unsafe.getChar(address));
                case INT -> new Value.Int(unsafe.getInt(address));
                case LONG -> new Value.Long(unsafe.getLong(address));
                case FLOAT -> new Value.Float(unsafe.getFloat(address));
                case DOUBLE -> new Value.Double(unsafe.getDouble(address));
                case REFERENCE, ARRAY -> Value.Reference.from(unsafe.getAddress(address));
            };
        } catch (Throwable e) {
            throw new MemoryManagmentException("Unable to get value from memory", e);
        }
    }

    public int getClassId(long objectAddress) throws MemoryManagmentException {
        try {
            return unsafe.getInt(objectAddress + MARK_WORD_HEADER_SIZE);
        } catch (Throwable e) {
            throw new MemoryManagmentException(e);
        }
    }


    private long computeTotalObjectSize(long payloadSize) {
        return OBJECT_HEADERS_SIZE + payloadSize;
    }
}
