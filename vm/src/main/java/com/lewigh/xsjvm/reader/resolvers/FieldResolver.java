package com.lewigh.xsjvm.reader.resolvers;


import com.lewigh.xsjvm.reader.info.FieldInfo;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class FieldResolver {

    public static FieldInfo[] resolve(InputStream is, ConstantPool constantPool, String className) throws IOException {
        int fieldsLen = readAsShort(is);
        var fields = new FieldInfo[fieldsLen];

        for (int i = 0; i < fieldsLen; i++) {
            var accessFlags = readAsShort(is);
            String name = constantPool.resolveUtf8Ref(readAsShort(is));
            String descriptor = constantPool.resolveUtf8Ref(readAsShort(is));
            short attributesCount = readAsShort(is);

            fields[i] = new FieldInfo(
                    accessFlags,
                    name,
                    descriptor,
                    AttributeResolver.resolve(
                            is,
                            attributesCount,
                            constantPool,
                            className,
                            name
                    )
            );

        }
        return fields;
    }
}
