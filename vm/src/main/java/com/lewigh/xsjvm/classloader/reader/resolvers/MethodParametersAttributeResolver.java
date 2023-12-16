package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.info.attribute.MethodParametersAttribute;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsByte;
import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class MethodParametersAttributeResolver implements AttributeResolvingStrategy<MethodParametersAttribute> {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is,
                                 String attName,
                                 int attLen,
                                 ConstantPool pool,
                                 String className,
                                 String target) throws IOException {

            byte parametersCount = readAsByte(is);

            var parameters = new MethodParametersAttribute.Parameter[parametersCount];

            for (int i = 0; i < parameters.length; i++) {
                short nameIndex = pool.checkOrZero(readAsShort(is));
                short accessFlags = pool.checkOrZero(readAsShort(is));
                parameters[i] = new MethodParametersAttribute.Parameter(
                        nameIndex,
                        accessFlags
                );
            }

            return new MethodParametersAttribute(
                    attName,
                    attLen,
                    parametersCount,
                    parameters
            );
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.MethodParameters);
    }
}
