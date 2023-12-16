package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.info.attribute.LocalVariableTableAttribute;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

class LocalVaribleTableAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {
        short localVariableTableLength = readAsShort(is);
        var localVariableTableElements = new LocalVariableTableAttribute.LocalVariableTableElement[localVariableTableLength];

        for (int i = 0; i < localVariableTableLength; i++) {
            short startPc = readAsShort(is);
            short length = readAsShort(is);
            short nameIndex = readAsShort(is);
            short descriptorIndex = readAsShort(is);
            short index = readAsShort(is);

            var element = new LocalVariableTableAttribute.LocalVariableTableElement(startPc, length, nameIndex, descriptorIndex, index);
            localVariableTableElements[i] = element;
        }

        return new LocalVariableTableAttribute(attName, attLen, localVariableTableLength, localVariableTableElements);
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.LOCAL_VARIABLE_TABLE_ATT);
    }
}
