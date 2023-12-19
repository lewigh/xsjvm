package com.lewigh.xsjvm.classloader.reader.info.attribute;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record Instruction(@NonNull OpCode opCode, @NonNull short[] operamds) {

    public short firsOperand() {
        if (operamds.length == 0) {
            throw new IllegalStateException();
        }
        return operamds[0];
    }

    public short secondOperand() {
        if (operamds.length == 0) {
            throw new IllegalStateException();
        }
        return operamds[1];
    }

    public short thirdOperand() {
        if (operamds.length == 0) {
            throw new IllegalStateException();
        }
        return operamds[2];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return opCode == that.opCode && Arrays.equals(operamds, that.operamds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(opCode);
        result = 31 * result + Arrays.hashCode(operamds);
        return result;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opCode=" + opCode +
                ", operamds=" + Arrays.toString(operamds) +
                '}';
    }
}
