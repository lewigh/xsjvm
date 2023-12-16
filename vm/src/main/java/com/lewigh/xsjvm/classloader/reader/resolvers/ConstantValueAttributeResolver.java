package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.info.attribute.ConstantValueAttribute;
import com.lewigh.xsjvm.classloader.reader.info.attribute.MethodParametersAttribute;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class ConstantValueAttributeResolver implements AttributeResolvingStrategy<MethodParametersAttribute> {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is,
                                 String attName,
                                 int attLen,
                                 ConstantPool pool,
                                 String className,
                                 String target) throws IOException {

        short constantvalue_index = readAsShort(is);

        return new ConstantValueAttribute(attName, attLen, constantvalue_index);
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.ConstantValue);
    }
}
