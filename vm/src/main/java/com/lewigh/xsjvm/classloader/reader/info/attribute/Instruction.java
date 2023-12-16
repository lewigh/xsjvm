package com.lewigh.xsjvm.classloader.reader.info.attribute;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record Instruction(@NonNull OpCode opCode, @NonNull byte[] arguments) {

    public byte firstdByteArg() {
        return arguments[0];
    }

    public int firstDoubledByteArg() {
        return (arguments[0] << 8) | arguments[1] & 0xff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return opCode == that.opCode && Arrays.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(opCode);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opCode=" + opCode +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
