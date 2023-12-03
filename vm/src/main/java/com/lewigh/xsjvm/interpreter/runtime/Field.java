package com.lewigh.xsjvm.interpreter.runtime;

public record Field(
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
