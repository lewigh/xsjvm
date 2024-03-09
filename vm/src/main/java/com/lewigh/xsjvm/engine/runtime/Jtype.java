package com.lewigh.xsjvm.engine.runtime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public sealed interface Jtype {

    Primitive primitive();

    boolean isPrimitive();

    record Reference(@NonNull String className) implements Jtype {
        @Override
        public Primitive primitive() {
            return Primitive.REFERENCE;
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }
    }

    record Array(@NonNull Jtype jtype) implements Jtype {

        @Override
        public Primitive primitive() {
            return Primitive.ARRAY;
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }
    }

    @RequiredArgsConstructor
    @Getter
    enum Primitive implements Jtype {
        BYTE('B', Size.B1, Size.B1),
        BOOL('Z', Size.B1, Size.B1),
        SHORT('S', Size.B2, Size.B2),
        INT('I', Size.B4, Size.B4),
        CHAR('C', Size.B4, Size.B4),
        FLOAT('F', Size.B4, Size.B4),
        LONG('J', Size.B8, Size.B8),
        DOUBLE('D', Size.B8, Size.B8),
        REFERENCE('L', Size.B8, Size.B8),
        VOID('V', Size.ZERO, Size.ZERO),
        ARRAY('[', Size.UNKNOWN, Size.UNKNOWN);

        private final char code;
        private final Size size;
        private final Size align;

        public static Primitive getTypeByCode(char code) {
            for (var value : values()) {
                if (value.code == code) {
                    return value;
                }
            }
            throw new IllegalArgumentException("There is no type match for the specified %s code".formatted(code));
        }

        public boolean hasCode(char code) {
            return this.code == code;
        }

        @Override
        public Primitive primitive() {
            return this;
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    }
}
