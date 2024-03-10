package com.lewigh.xsjvm.gc;

import java.nio.ByteBuffer;

public class ByteBufferMemoryAllocator implements MemoryAllocator {

    public static final int CHANK_SIZE = 16;
    public static final int META_SIZE = 8;
    public static final int PAYLOAD_SIZE = CHANK_SIZE - META_SIZE;

    private final ByteBuffer heap;

    private int spaceSize;

    private int allocatorPosition;

    private ByteBufferMemoryAllocator(ByteBuffer heap, int spaceSize) {
        this.heap = heap;
        this.spaceSize = spaceSize;
    }

    public static ByteBufferMemoryAllocator create(int spaceSize) {
        var allocator = new ByteBufferMemoryAllocator(ByteBuffer.allocate(spaceSize), spaceSize);
        allocator.init();
        return allocator;
    }

    private void init() {
        for (int i = 0; i < spaceSize; i += CHANK_SIZE) {
            heap.putInt(i, PAYLOAD_SIZE);
            heap.putInt(i + 4, 0);
        }
    }

    public int findFirstFreeChunk(int from, int size) {
        return findFirstChunkWithPart(from, 0, size);
    }

    public int findFirstChunkWithPart(int from, int occupied, int size) {
        int cur = from;

        for (; ; ) {
            if (getOccupied(cur) == occupied && (size == -1 || getPayloadSize(cur) >= size)) {
                return cur;
            }
            cur = (cur + getChunkSize(cur)) % spaceSize;
            if (cur == from) {
                return -1;
            }
        }
    }

    public int findFirstFreeChunk(int from) {
        int cur = from;

        for (; ; ) {
            if (getOccupied(cur) == 0) {
                return cur;
            }
            cur = (cur + getChunkSize(cur)) % spaceSize;
            if (cur == from) {
                return -1;
            }
        }
    }


    public int useChunk(int from, int size) {

        int freeChunk = findFirstFreeChunk(from);

        if (freeChunk == -1) {
            throw new IllegalStateException("Memory out");
        }

        int payloadSize = getPayloadSize(freeChunk);

        if (payloadSize < size) {
            int total = payloadSize;
            int cur = freeChunk;
            int next = next(cur);

            while (total < size) {
                if (next >= spaceSize) {
                    throw new IllegalStateException("Out of memory");
                }
                total += getChunkSize(next);
                cur = next;
                next = next(cur);
            }

            setOccupied(freeChunk, 1);
            setSize(freeChunk, total);

        }
        return freeChunk;
    }


    private int getPayloadSize(int cur) {
        return heap.getInt(cur);
    }

    private int getChunkSize(int address) {
        return getPayloadSize(address) + META_SIZE;
    }

    private int getOccupied(int cur) {
        return heap.getInt(cur + 4);
    }

    private void setSize(int chunkAddr, int size) {
        heap.putInt(chunkAddr, size);
    }

    private void setOccupied(int chunkAddr, int x) {
        heap.putInt(chunkAddr + 4, x);
    }

    private int next(int addr) {
        return addr + getChunkSize(addr);
    }

    public int allocate(int size) {

        int target = useChunk(allocatorPosition, size);

        allocatorPosition += getChunkSize(target);

        return target;
    }

    public boolean free(int address) {
        if (address % CHANK_SIZE != 0) {
            throw new IllegalStateException("Incorrect address for free %d".formatted(address));
        }

        int flagPointer = address + 4;

        int freeFlag = heap.getInt(flagPointer);

        if (freeFlag == 1) {
            heap.putInt(flagPointer);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public byte getByte(long address) {
        return heap.get((int) address + META_SIZE);
    }

    @Override
    public void putByte(long address, byte x) {
        heap.put((int) address + META_SIZE, x);
    }

    @Override
    public short getShort(long address) {
        return heap.getShort((int) address + META_SIZE);
    }

    @Override
    public void putShort(long address, short x) {
        heap.putShort((int) address + META_SIZE, x);
    }

    @Override
    public char getChar(long address) {
        return heap.getChar((int) address + META_SIZE);
    }

    @Override
    public void putChar(long address, char x) {
        heap.putChar((int) address + META_SIZE, x);
    }

    @Override
    public int getInt(long address) {
        return heap.getInt((int) address + META_SIZE);
    }

    @Override
    public void putInt(long address, int x) {
        heap.putInt((int) address + META_SIZE, x);
    }

    @Override
    public long getLong(long address) {
        return heap.getLong((int) address + META_SIZE);
    }

    @Override
    public void putLong(long address, long x) {
        heap.putLong((int) address + META_SIZE, x);
    }

    @Override
    public float getFloat(long address) {
        return heap.getFloat((int) address + META_SIZE);
    }

    @Override
    public void putFloat(long address, float x) {
        heap.putFloat((int) address + META_SIZE, x);
    }

    @Override
    public double getDouble(long address) {
        return heap.getDouble((int) address + META_SIZE);
    }

    @Override
    public void putDouble(long address, double x) {
        heap.putDouble((int) address + META_SIZE, x);
    }

    @Override
    public long getAddress(long address) {
        return heap.getInt((int) address + META_SIZE);
    }

    @Override
    public void putAddress(long address, long x) {
        heap.putInt((int) address + META_SIZE, (int) x);
    }

    @Override
    public long allocateMemory(long bytes) {
        int address = allocate((int) bytes);
        if (address == -1) {
            throw new IllegalStateException("Unable to allocate memory size %d. Free memory is not found".formatted(bytes));
        }
        return address;
    }
}
