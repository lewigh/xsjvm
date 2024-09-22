package com.lewigh.xsjvm.mem;

import com.lewigh.xsjvm.MemoryManagmentException;
import com.lewigh.xsjvm.engine.runtime.FieldDesc;
import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.Value;

import java.util.Collection;

public class StandartVmMemoryManager implements VmMemoryManager {

    private static final int MARK_WORD_HEADER_SIZE = 4;
    private static final int CLASS_WORD_HEADER_SIZE = 4;
    private static final int ARRAY_HEADER_SIZE = 4;
    private static final int OBJECT_HEADERS_SIZE = MARK_WORD_HEADER_SIZE + CLASS_WORD_HEADER_SIZE;

    private final MemoryAllocator allocator;

    public StandartVmMemoryManager(MemoryAllocator allocator) {
        this.allocator = allocator;
    }

    @Override
    public long allocateObject(int classId, Collection<FieldDesc> fields, long payloadSize) throws MemoryManagmentException {
        try {
            long objectAddress = allocator.allocate(computeTotalObjectSize(payloadSize));

            long cursor = objectAddress;

            cursor += MARK_WORD_HEADER_SIZE;

            allocator.putInt(cursor, classId);

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

    @Override
    public long allocateArray(Jtype.Primitive type, int size) throws MemoryManagmentException {
        try {

            int payloadSize = type.getAlign().getTotal() * size;

            int totalSize = OBJECT_HEADERS_SIZE + payloadSize;

            long objectAddress = allocator.allocate(totalSize);

            long cursor = objectAddress;

            cursor += MARK_WORD_HEADER_SIZE;

            allocator.putInt(cursor, 0);

            cursor += CLASS_WORD_HEADER_SIZE;

            allocator.putInt(cursor, size);

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

    @Override
    public long allocateArray(int classId, Collection<FieldDesc> fields, long payloadSize, int size) {
        long totalSize = OBJECT_HEADERS_SIZE + computeTotalObjectSize(payloadSize) * size;

        long objectAddress = allocator.allocate(totalSize);

        long cursor = objectAddress;

        cursor += MARK_WORD_HEADER_SIZE;

        allocator.putInt(cursor, classId);

        cursor += CLASS_WORD_HEADER_SIZE;

        allocator.putInt(cursor, size);

        cursor += ARRAY_HEADER_SIZE;

        for (int i = 0; i < size; i++) {
            for (var field : fields) {
                var type = field.type().primitive();

                initWithType(cursor, type);
                cursor += type.getAlign().getTotal();
            }
        }

        return objectAddress;
    }

    @Override
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

    @Override
    public Value getArrayElement(long address, int index, Jtype.Primitive type) throws MemoryManagmentException {

        try {
            var cursor = address + OBJECT_HEADERS_SIZE;

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

    @Override
    public void putWithType(long address, Jtype.Primitive type, Number value) throws MemoryManagmentException {
        try {
            switch (type) {
                case VOID -> {
                }
                case BYTE, BOOL -> {
                    allocator.putByte(address, (byte) value);
                }
                case SHORT -> allocator.putShort(address, (short) value);
                case CHAR -> allocator.putChar(address, (char) ((int) value));
                case INT -> allocator.putInt(address, value.intValue());
                case LONG -> allocator.putLong(address, (long) value);
                case FLOAT -> allocator.putFloat(address, (float) value);
                case DOUBLE -> allocator.putDouble(address, (double) value);
                case REFERENCE, ARRAY -> allocator.putAddress(address, (long) value);
            }
        } catch (Throwable e) {
            throw new MemoryManagmentException("Unable to put value into memory", e);
        }
    }

    @Override
    public void initWithType(long address, Jtype.Primitive type) throws MemoryManagmentException {
        try {


            switch (type) {
                case VOID -> {
                }
                case BYTE, BOOL -> allocator.putByte(address, (byte) 0);
                case SHORT -> allocator.putShort(address, (short) 0);
                case CHAR -> allocator.putChar(address, (char) 0);
                case INT -> allocator.putInt(address, 0);
                case LONG -> allocator.putLong(address, 0L);
                case FLOAT -> allocator.putFloat(address, 0.0f);
                case DOUBLE -> allocator.putDouble(address, 0d);
                case REFERENCE, ARRAY -> allocator.putAddress(address, 0L);
            }
        } catch (Throwable e) {
            throw new MemoryManagmentException(e);
        }
    }

    @Override
    public Value getWithType(long address, Jtype.Primitive type) throws MemoryManagmentException {
        try {
            return switch (type) {
                case VOID -> {
                    throw new RuntimeException();
                }
                case BYTE -> new Value.Byte(allocator.getByte(address));
                case BOOL -> new Value.Bool(allocator.getByte(address) != 0);
                case SHORT -> new Value.Short(allocator.getShort(address));
                case CHAR -> new Value.Char(allocator.getChar(address));
                case INT -> new Value.Int(allocator.getInt(address));
                case LONG -> new Value.Long(allocator.getLong(address));
                case FLOAT -> new Value.Float(allocator.getFloat(address));
                case DOUBLE -> new Value.Double(allocator.getDouble(address));
                case REFERENCE, ARRAY -> Value.Ref.from(allocator.getAddress(address));
            };
        } catch (Throwable e) {
            throw new MemoryManagmentException("Unable to get value from memory", e);
        }
    }

    @Override
    public int getClassId(long objectAddress) throws MemoryManagmentException {
        try {
            return allocator.getInt(objectAddress + MARK_WORD_HEADER_SIZE);
        } catch (Throwable e) {
            throw new MemoryManagmentException(e);
        }
    }


    private long computeTotalObjectSize(long payloadSize) {
        return OBJECT_HEADERS_SIZE + payloadSize;
    }
}
