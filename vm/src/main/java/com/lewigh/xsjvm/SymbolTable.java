package com.lewigh.xsjvm;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SymbolTable {

    public static final String ENTRY_POINT_METHOD__NAME = "main";
    public static final String ENTRY_POINT_METHOD__DESC = "([Ljava/lang/String;)V";
    public static final String CLINIT_METH_FULL_NAME = "<clinit>()V";
}
