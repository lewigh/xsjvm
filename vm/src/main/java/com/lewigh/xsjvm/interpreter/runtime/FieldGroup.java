package com.lewigh.xsjvm.interpreter.runtime;

import java.util.LinkedHashMap;

public record FieldGroup(
        LinkedHashMap<String, Field> fields,
        long instanceSize,
        long staticSize
) {
}
