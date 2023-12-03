package com.lewigh.xsjvm.reader.resolvers;


import com.lewigh.xsjvm.reader.info.MethodInfo;
import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class MethodResolver {

    public static MethodInfo[] resolve(@NonNull InputStream is, @NonNull ConstantPool cp, @NonNull String className) throws IOException {
        int methodLen = readAsShort(is);
        var methods = new MethodInfo[methodLen];

        for (int i = 0; i < methodLen; i++) {
            try {
                short accessFlags = readAsShort(is);
                short nameIndex = readAsShort(is);
                String name = cp.resolveUtf8Ref(nameIndex);
                short descIndex = readAsShort(is);
                String descriptor = cp.resolveUtf8Ref(descIndex);
                short attributesCount = readAsShort(is);

                AttributeInfo[] attributes = AttributeResolver.resolve(is, attributesCount, cp, className, name + descriptor);

                methods[i] = new MethodInfo(accessFlags, name, descriptor, attributesCount, attributes);

            } catch (RuntimeException e) {
                throw new IllegalStateException("Problem when processing methods of the %s class".formatted(className), e);
            }
        }
        return methods;
    }
}
