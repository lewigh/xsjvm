package com.lewigh.xsjvm.reader.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum FiledAccessFlag {
    ACC_PUBLIC(0x0001),
    ACC_PRIVATE(0x0002),
    ACC_PROTECTED(0x0004),
    ACC_STATIC(0x0008),
    ACC_FINAL(0x0010),
    ACC_VOLATILE(0x0040),
    ACC_TRANSIENT(0x0080),
    ACC_SYNTHETIC(0x1000),
    ACC_ENUM(0x4000);

    private final int hex;

    public static FiledAccessFlag[] parse(int mask) {
        return Stream.of(values())
                .filter(f -> (mask & f.hex) == f.hex)
                .toArray(FiledAccessFlag[]::new);
    }

}
