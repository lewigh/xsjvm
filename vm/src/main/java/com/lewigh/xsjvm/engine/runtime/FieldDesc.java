package com.lewigh.xsjvm.engine.runtime;

public record FieldDesc(
        String name,
        Jtype type,
        Access access,
        boolean accStatic,
        boolean accFinal,
        boolean accVolatile,
        boolean accTransient,
        boolean accSynthetic,
        boolean accEnum,
        long offset
) {
}
