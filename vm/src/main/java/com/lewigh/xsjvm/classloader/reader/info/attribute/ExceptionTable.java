package com.lewigh.xsjvm.classloader.reader.info.attribute;

public record ExceptionTable(
        short start_pc,
        short end_pc,
        short handler_pc,
        short catch_type
) {
}
