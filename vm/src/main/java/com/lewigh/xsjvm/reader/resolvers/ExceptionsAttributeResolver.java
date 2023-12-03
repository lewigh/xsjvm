package com.lewigh.xsjvm.reader.resolvers;

import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.info.attribute.ExceptionsAttribute;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class ExceptionsAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {


        short number_of_exceptions = readAsShort(is);

        short[] exception_index_table = new short[number_of_exceptions];

        for (int i = 0; i < number_of_exceptions; i++) {
            exception_index_table[i] = readAsShort(is);
        }

        return new ExceptionsAttribute(attName, attLen, number_of_exceptions, exception_index_table);
    }

    @Override
    public boolean supports(String name) {
        return name.equals("Exceptions");
    }
}
