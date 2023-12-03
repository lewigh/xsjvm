package com.lewigh.xsjvm.reader.resolvers;


import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.info.attribute.ConstantValueAttribute;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsInt;
import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class AttributeResolver {

    public static AttributeInfo[] resolve(@NonNull InputStream is,
                                          short attributesCount,
                                          @NonNull ConstantPool pool,
                                          @NonNull String className,
                                          @NonNull String target) throws IOException {

        var attributes = new AttributeInfo[attributesCount];

        List<AttributeResolvingStrategy> resolvers = List.of(
                new CodeAttributeResolver(),
                new LineNumberTableAttributeResolver(),
                new LocalVaribleTableAttributeResolver(),
                new LocalVaribleTypeTableAttributeResolver(),
                new RuntimeVisibleAnnotationsAttributeResolver(),
                new SignatureAttributeResolver(),
                new StackMapTableAttributeResolver(),
                new ExceptionsAttributeResolver(),
                new DeprecatedAttributeResolver(),
                new MethodParametersAttributeResolver(),
                new ConstantValueAttributeResolver()
        );

        for (int i = 0; i < attributesCount; i++) {
            short nameIndex = readAsShort(is);
            String attrName = pool.resolveUtf8Ref(nameIndex);
            int attrLength = readAsInt(is);

            try {
                boolean filed = false;
                for (var resolver : resolvers) {
                    if (resolver.supports(attrName)) {
                        AttributeInfo resolved = resolver.resolve(is, attrName, attrLength, pool, className, target);
                        if (resolved == null) {
                            throw new IllegalStateException("The %s attribute cannot be null".formatted(attrName));
                        }
                        filed = true;
                        attributes[i] = resolved;
                    }
                }

                if (!filed) {
                    throw new IllegalStateException("The %s attribute is not set".formatted(attrName));
                }

            } catch (RuntimeException e) {
                throw new IllegalStateException(
                        "An error occurred while processing attribute %s of method %s of class %s previous attributes:[%s]"
                                .formatted(attrName, target, className, Arrays.stream(attributes).filter(a -> a != null)
                                        .map(a -> a.toString())
                                        .collect(Collectors.joining(","))),
                        e);
            }
        }
        return attributes;
    }
}
