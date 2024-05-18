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

        @Override
        public String toString() {
            return "Byte:" + value;
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

        @Override
        public String toString() {
            return "Bool:" + value;
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

        @Override
        public String toString() {
            return "Short:" + value;
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

        @Override
        public String toString() {
            return "Char:" + value;
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

        @Override
        public String toString() {
            return "Int:" + value;
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

        @Override
        public String toString() {
            return "Long:" + value;
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

        @Override
        public String toString() {
            return "Float:" + value;
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

        @Override
        public String toString() {
            return "Double:" + value;
        }
    }

    @Getter
    @EqualsAndHashCode
    non-sealed class Ref implements Value {

        private final long value;

        private Ref(long ref) {
            this.value = ref;
        }

        public static Ref from(long ref) {
            if (ref == 0) {
                return new Null();
            }
            return new Ref(ref);
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

        @Override
        public String toString() {
            return "Ref:" + value;
        }
    }

    class Null extends Ref {
        public Null() {
            super(0);
        }

        @Override
        public String toString() {
            return "Null";
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
        if (this instanceof Ref reference) {
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
