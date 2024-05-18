package com.lewigh.xsjvm.engine.runtime;

import java.util.LinkedHashMap;

public record FieldDescGroup(
        LinkedHashMap<String, FieldDesc> fields,
        long instanceSize,
        long staticSize
) {
}
