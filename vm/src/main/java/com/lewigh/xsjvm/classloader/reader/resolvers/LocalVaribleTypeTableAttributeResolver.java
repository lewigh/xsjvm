package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.info.attribute.LocalVariableTypeTableAttribute;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class LocalVaribleTypeTableAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {
        short localVariableTableTypeLength = readAsShort(is);
        var localVariableTableElements = new LocalVariableTypeTableAttribute.LocalVariableTypeTableElement[localVariableTableTypeLength];

        for (int i = 0; i < localVariableTableTypeLength; i++) {
            short startPc = readAsShort(is);
            short length = readAsShort(is);
            short nameIndex = readAsShort(is);
            short descriptorIndex = readAsShort(is);
            short index = readAsShort(is);

            var element = new LocalVariableTypeTableAttribute.LocalVariableTypeTableElement(startPc, length, nameIndex, descriptorIndex, index);
            localVariableTableElements[i] = element;
        }

        return new LocalVariableTypeTableAttribute(attName, attLen, localVariableTableTypeLength, localVariableTableElements);
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.LOCAL_VARIABLE_TYPE_TABLE_ATT);
    }
}
