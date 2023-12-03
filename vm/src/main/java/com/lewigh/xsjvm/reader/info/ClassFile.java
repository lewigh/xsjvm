package com.lewigh.xsjvm.reader.info;


import com.lewigh.xsjvm.reader.flag.ClassAccessFlag;
import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.pool.ConstantPool;

public record ClassFile(short major,
                        short minor,
                        ClassAccessFlag[] accessFlags,
                        ConstantPool constantPool,
                        String thisName,
                        String superName,
                        String[] interfaces,
                        FieldInfo[] fields,
                        MethodInfo[] methods,
                        AttributeInfo[] attributes) {

}
