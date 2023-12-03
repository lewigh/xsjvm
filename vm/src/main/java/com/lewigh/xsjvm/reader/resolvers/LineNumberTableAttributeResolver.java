package com.lewigh.xsjvm.reader.resolvers;

import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.info.attribute.LineNumberTableAttribute;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

class LineNumberTableAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {
        short lineNumberTableLength = readAsShort(is);
        var lineNumberTable = new LineNumberTableAttribute.LineNumberTable[lineNumberTableLength];

        for (int i = 0; i < lineNumberTableLength; i++) {
            short startPc = readAsShort(is);
            short lineNumber = readAsShort(is);
            lineNumberTable[i] = new LineNumberTableAttribute.LineNumberTable(startPc, lineNumber);
        }

        return new LineNumberTableAttribute(attName, attLen, lineNumberTableLength, lineNumberTable);
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.LINE_NUMBER_TABLE_ATT);
    }
}
