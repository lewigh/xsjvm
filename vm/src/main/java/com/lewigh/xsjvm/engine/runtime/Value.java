package com.lewigh.xsjvm.engine.runtime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public sealed interface Value {

    record Byte(byte value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    record Bool(boolean value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value ? 1 : 0;
        }
    }

    record Short(short value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    record Char(char value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return (int) value;
        }
    }

    record Int(int value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    record Long(long value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    record Float(float value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    record Double(double value) implements Value {
        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    non-sealed class Reference implements Value {

        private final long value;

        private Reference(long ref) {
            this.value = ref;
        }

        public static Reference from(long ref) {
            if (ref == 0) {
                return new Null();
            }
            return new Reference(ref);
        }

        public boolean isNull() {
            return value == 0;
        }

        @Override
        public Object getVal() {
            return value;
        }

        @Override
        public Number asNumber() {
            return value;
        }
    }

    class Null extends Reference {
        public Null() {
            super(0);
        }
    }

    Object getVal();

    Number asNumber();

    default byte asByte() {
        if (this instanceof Byte b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    default boolean asBool() {
        if (this instanceof Bool b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default short asSshort() {
        if (this instanceof Short b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default char asChar() {
        if (this instanceof Char b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default int asInt() {
        if (this instanceof Int b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default long asLong() {
        if (this instanceof Long b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default float asFloat() {
        if (this instanceof Float b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default double asDouble() {
        if (this instanceof Double b) {
            return b.value();
        }
        throw throwE(getVal());
    }

    default long asRef() {
        if (this instanceof Reference reference) {
            if (reference instanceof Null) {
                throw new NullPointerException();
            }
            return reference.value;
        }
        throw throwE(getVal());
    }

    private IllegalArgumentException throwE(Object wrong) {
        return new IllegalArgumentException("The wrong type. Passed:%s should be %s".formatted(wrong.getClass().getSimpleName(), this.getClass().getSimpleName()));
    }
}
