package com.lewigh.xsjvm.reader.info.attribute;

import lombok.Getter;

public record ConstantValueAttribute(
        String attributeName,
        int attributeLength,
        short constantvalue_index

) implements AttributeInfo {

    @Getter
    enum Type {
        CONSTANT_INTEGER("CONSTANT_Integer", "int", "short", "char", "byte", "boolean"),
        CONSTANT_FLOAT("CONSTANT_Float", "float"),
        CONSTANT_LONG("CONSTANT_Long", "long"),
        CONSTANT_DOUBLE("CONSTANT_Double", "long"),
        CONSTANT_STRING("CONSTANT_String", "String");

        private final String name;
        private final String[] types;

        Type(String name, String... type) {
            this.name = name;
            this.types = type;
        }
    }
}
