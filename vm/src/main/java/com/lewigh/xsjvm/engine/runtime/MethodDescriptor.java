package com.lewigh.xsjvm.engine.runtime;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public record MethodDescriptor(@NonNull Jtype[] paarameterTypes, @NonNull Jtype returnType) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodDescriptor that = (MethodDescriptor) o;
        return Arrays.equals(paarameterTypes, that.paarameterTypes) && Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(returnType);
        result = 31 * result + Arrays.hashCode(paarameterTypes);
        return result;
    }

    @Override
    public String toString() {
        return "MethodDescriptor{" +
                "paarameterTypes=" + Arrays.toString(paarameterTypes) +
                ", returnType=" + returnType +
                '}';
    }
}
