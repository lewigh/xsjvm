package com.lewigh.xsjvm.reader.info.attribute;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypeTag {
    T_BYTE('B'),
    T_CHAR('C'),
    T_DOUBLE('D'),
    T_FLOAT('F'),
    T_INT('I'),
    T_LONG('J'),
    T_SHORT('S'),
    T_BOOLEAN('Z'),
    T_STRING('s'),
    T_ENUM_CLASS('e'),
    T_CLASS('c'),
    T_ANNOTATION_INTERFACE('@'),
    T_ARRAY_TYPE('[');

    private final char tag;

    public static TypeTag getByTag(char tag) {
        for (var value : values()) {
            if (value.tag == tag) {
                return value;
            }
        }
        throw new IllegalArgumentException("The %c tag is not supported".formatted(tag));
    }
}
