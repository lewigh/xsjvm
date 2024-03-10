package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.MethodDescriptor;
import lombok.NonNull;

import java.util.ArrayList;

public class DescriptorResolver {

    public static final int PREFIX_GAP = 1;
    public static final int SEMICOLUMN_GAP = 1;

    public record TypeAndGap(Jtype type, int gap) {
    }

    @NonNull
    public static MethodDescriptor resolveMethodDescriptor(@NonNull String descriptor) {
        try {

            var buff = new ArrayList<Jtype>();

            int current = 1;

            while (descriptor.charAt(current) != ')') {
                var typeAndGap = resolveTypeAndGap(descriptor, current);

                buff.add(typeAndGap.type());

                current += typeAndGap.gap();

                current++;
            }

            current++;

            Jtype returnType = resolveTypeAndGap(descriptor, current).type();
            Jtype[] paramTypes = buff.toArray(Jtype[]::new);

            return new MethodDescriptor(paramTypes, returnType);

        } catch (Exception e) {
            throw new IllegalStateException("Error has occured while parsing type %s".formatted(descriptor), e);
        }
    }

    @NonNull
    public static Jtype resolveType(@NonNull String descriptor) {
        return resolveTypeAndGap(descriptor, 0).type();
    }

    private static TypeAndGap resolveTypeAndGap(String descriptor, int from) {
        var type = Jtype.Primitive.getTypeByCode(descriptor.charAt(from));

        return switch (type) {
            case REFERENCE -> resolverRefType(descriptor, from);
            case ARRAY -> resolveArrayType(descriptor, from);
            default -> resolvePremitiveType(type);
        };
    }

    private static TypeAndGap resolverRefType(String strDesc, int from) {
        for (int i = from + PREFIX_GAP, j = PREFIX_GAP; i < strDesc.length(); i++, j++) {
            if (strDesc.charAt(i) == ';') {
                var className = strDesc.substring(from + PREFIX_GAP, i);

                return new TypeAndGap(new Jtype.Reference(className), j);
            }
        }
        throw new IllegalStateException("Can not able to parse type. Internal error with ref/arr type %s".formatted(strDesc));
    }

    private static TypeAndGap resolveArrayType(String strDesc, int from) {
        TypeAndGap typeAndGap = resolveTypeAndGap(strDesc, from + PREFIX_GAP);

        Jtype subtype = typeAndGap.type();

        if (subtype instanceof Jtype.Primitive p && (p == Jtype.Primitive.REFERENCE || p == Jtype.Primitive.VOID)) {
            throw new IllegalStateException("Incompatible type %s for array".formatted(subtype));
        }

        return new TypeAndGap(new Jtype.Array(subtype), PREFIX_GAP + typeAndGap.gap);
    }

    private static TypeAndGap resolvePremitiveType(Jtype.Primitive type) {
        return new TypeAndGap(type, 0);
    }

}
