package com.lewigh.xsjvm.reader.info.attribute;

public record RuntimeVisibleAnnotationsAttribute(
        String attributeName,
        int attributeLength,
        short numAnnotations,
        Annotation[] annotations
) implements AttributeInfo {

    public record Annotation(
            short typeIndex,
            short numElementValuePairs,
            ElementValuePairs[] elementValuePairs

    ) {
        public record ElementValuePairs(short elementNameIndex, ElementValue value) {

        }

    }
}
