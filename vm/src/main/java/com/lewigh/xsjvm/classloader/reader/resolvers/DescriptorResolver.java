package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.MethodDescriptor;
import lombok.NonNull;

import java.util.ArrayList;

public class DescriptorResolver {

    public static final int PREFIX_GAP = 1;
    public static final int SEMICOLUMN_GAP = 1;

    @NonNull
    public static MethodDescriptor resolveMethodDescriptor(@NonNull String descriptor) {
        try {

            int lastParamIdx = descriptor.lastIndexOf(')') + 1;
            var paramsPart = descriptor.substring(1, lastParamIdx - 1);
            var retPart = descriptor.substring(lastParamIdx);

            var types = new ArrayList<Jtype>();

            int current = 0;

            while (current < paramsPart.length()) {
                var typeAndGap = resolveTypeAndSize(paramsPart, current);

                types.add(typeAndGap.type());

                current += typeAndGap.gap();

                current++;
            }

            Jtype retType = resolveType(retPart);
            Jtype[] paramTypes = new Jtype[types.size()];

            for (int i = 0; i < paramTypes.length; i++) {
                paramTypes[i] = types.get(i);
            }

            return new MethodDescriptor(paramTypes, retType);

        } catch (Exception e) {
            throw new IllegalStateException("Error has occured while parsing type %s".formatted(descriptor), e);
        }
    }

    @NonNull
    public static Jtype resolveType(@NonNull String descriptor) {
        return resolveTypeAndSize(descriptor, 0).type();
    }

    private static TypeAndGap resolveTypeAndSize(String descriptor, int current) {
        var type = Jtype.Primitive.getTypeByCode(descriptor.charAt(current));

        return switch (type) {
            case REFERENCE -> resolverRefType(descriptor, current);
            case ARRAY -> resolveArrayType(descriptor, current);
            default -> resolvePremitiveType(type);
        };
    }

    private static TypeAndGap resolverRefType(String strDesc, int current) {
        for (int i = current + PREFIX_GAP, j = PREFIX_GAP; i < strDesc.length(); i++, j++) {
            if (strDesc.charAt(i) == ';') {
                var className = strDesc.substring(current + PREFIX_GAP, i);

                return new TypeAndGap(new Jtype.Reference(className), j);
            }
        }
        throw new IllegalStateException("Can not able to parse type. Internal error with ref/arr type %s".formatted(strDesc));
    }

    private static TypeAndGap resolveArrayType(String strDesc, int current) {
        TypeAndGap typeAndGap = resolveTypeAndSize(strDesc, current + PREFIX_GAP);

        Jtype subtype = typeAndGap.type();

        if (subtype instanceof Jtype.Primitive p && (p == Jtype.Primitive.REFERENCE || p == Jtype.Primitive.VOID)) {
            throw new IllegalStateException("Incompatible type %s for array".formatted(subtype));
        }

        return new TypeAndGap(new Jtype.Array(subtype), PREFIX_GAP + typeAndGap.gap);
    }

    private static TypeAndGap resolvePremitiveType(Jtype.Primitive type) {
        return new TypeAndGap(type, 0);
    }

    public record TypeAndGap(Jtype type, int gap) {
    }
}
