package com.lewigh.xsjvm.reader.resolvers;

import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.info.attribute.SignatureAttribute;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

@RequiredArgsConstructor
public class SignatureAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {

        short signatureIndex = readAsShort(is);

        return new SignatureAttribute(
                attName,
                attLen,
                signatureIndex
        );
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.Signature_ATT);
    }
}
