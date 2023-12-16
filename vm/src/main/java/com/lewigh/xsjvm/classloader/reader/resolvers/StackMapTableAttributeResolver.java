package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.info.attribute.StackMapTableAttribute;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

// FIXME TO FINISH LATER
public class StackMapTableAttributeResolver implements AttributeResolvingStrategy {


    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {

        short numberOfEntrie = readAsShort(is);

        is.readNBytes(attLen - 2);

        return new StackMapTableAttribute(
                attName,
                attLen,
                numberOfEntrie
        );
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.StackMapTable);
    }
}
