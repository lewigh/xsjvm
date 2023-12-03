package com.lewigh.xsjvm.reader.flag;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ClassAccessFlag {
    ACC_PUBLIC(0x0001),
    ACC_FINAL(0x0010),
    ACC_SUPER(0x0020),
    ACC_INTERFACE(0x0200),
    ACC_ABSTRACT(0x0400),
    ACC_SYNTHETIC(0x1000),
    ACC_ANNOTATION(0x2000),
    ACC_ENUM(0x4000),
    ACC_MODULE(0x8000);

    private final int hex;

    public static ClassAccessFlag[] parse(int mask) {
        return Stream.of(values())
                .filter(f -> (mask & f.hex) == f.hex)
                .toArray(ClassAccessFlag[]::new);
    }
}
