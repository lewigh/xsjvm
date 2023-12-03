package com.lewigh.xsjvm.interpreter.runtime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Size {
    ZERO((byte) 0),
    B1((byte) 1),
    B2((byte) 2),
    B4((byte) 4),
    B8((byte) 8),
    UNKNOWN((byte) 0);

    private final byte total;
}
