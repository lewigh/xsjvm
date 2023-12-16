package com.lewigh.xsjvm.classloader.reader;

public enum ConstantTag {
    UTF_8(1),
    INTEGER(3),
    FLOAT(4),
    LONG(5),
    DOUBLE(6),
    CLASS(7),
    STRING(8),
    FIELD_REF(9),
    METHOD_REF(10),
    INTERFACE_METHOD_REF(11),
    NAME_AND_TYPE(12),
    METHOD_HANDLE(15),
    METHOD_TYPE(16),
    DYNAMIC(17),
    INVOKE_DYNAMIC(18),
    MODULE(19),
    PACKAGE(20);

    private final int id;

    ConstantTag(int tag) {
        this.id = tag;
    }

    public int getId() {
        return id;
    }

}
