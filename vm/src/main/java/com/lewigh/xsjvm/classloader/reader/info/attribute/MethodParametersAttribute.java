package com.lewigh.xsjvm.classloader.reader.info.attribute;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record MethodParametersAttribute(
        String attributeName,
        int attributeLength,
        byte parameters_count,
        Parameter[] parameters
) implements AttributeInfo {

    public record Parameter(short nameIndex, short accessFlags) {

        @Getter
        @RequiredArgsConstructor
        enum Flag {
            // Indicates that the formal parameter was declared final
            ACC_FINAL(0x0010),
            // Indicates that the formal parameter was not explicitly or implicitly declared in source instructions,
            // according to the specification of the language in which the source instructions was written (JLS §13.1).
            // (The formal parameter is an implementation artifact of the compiler which produced this class file.)
            ACC_SYNTHETIC(0x1000),
            // Indicates that the formal parameter was implicitly declared in source instructions,
            // according to the specification of the language in which the source instructions was written (JLS §13.1).
            // (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)
            ACC_MANDATED(0x8000);

            private final int hex;
        }


    }
}
