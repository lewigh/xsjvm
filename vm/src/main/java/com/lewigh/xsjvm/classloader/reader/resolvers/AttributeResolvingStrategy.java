package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public interface AttributeResolvingStrategy<T extends AttributeInfo> {

    @NonNull
    AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException;

    boolean supports(String name);
}
