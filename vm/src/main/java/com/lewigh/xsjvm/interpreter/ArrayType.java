package com.lewigh.xsjvm.interpreter;

import com.lewigh.xsjvm.interpreter.runtime.Jtype;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArrayType {

    T_BOOLEAN((byte) 4, Jtype.Primitive.BOOL),
    T_CHAR((byte) 5, Jtype.Primitive.CHAR),
    T_FLOAT((byte) 6, Jtype.Primitive.FLOAT),
    T_DOUBLE((byte) 7, Jtype.Primitive.DOUBLE),
    T_BYTE((byte) 8, Jtype.Primitive.BYTE),
    T_SHORT((byte) 9, Jtype.Primitive.SHORT),
    T_INT((byte) 10, Jtype.Primitive.INT),
    T_LONG((byte) 11, Jtype.Primitive.LONG);

    private final byte code;
    private final Jtype.Primitive primitive;

    public static ArrayType byCode(byte code) {
        for (var t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }
}
