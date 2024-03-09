package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.MethodDescriptor;
import lombok.NonNull;

import java.util.ArrayList;

public class DescriptorResolver {

    public static final int PREFIX_GAP = 1;

    public static MethodDescriptor resolveMethodDescriptor(String strDesc) {
        try {

            int lastParamIdx = strDesc.lastIndexOf(')') + 1;
            var paramsPart = strDesc.substring(1, lastParamIdx - 1);
            var retPart = strDesc.substring(lastParamIdx);


            var types = new ArrayList<Jtype>();

            int current = 0;

            while (current < paramsPart.length()) {
                var typeAndGap = resolveTypeAndSize(paramsPart, current);

                types.add(typeAndGap.type());

                current += typeAndGap.size();
            }

            Jtype retType = resolveType(retPart);

            Jtype[] parameters = new Jtype[types.size()];

            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = types.get(i);
            }

            return new MethodDescriptor(parameters, retType);

        } catch (Exception e) {
            throw new IllegalStateException("Error has occured while parsing type %s".formatted(strDesc), e);
        }
    }

    @NonNull
    public static Jtype resolveType(String strDesc) {
        return resolveTypeAndSize(strDesc, 0).type();
    }

    private static TypeAndSize resolveTypeAndSize(String strDesc, int current) {
        char code = strDesc.charAt(current);

        if (Jtype.Primitive.REFERENCE.hasCode(code)) {
            return resolverRefType(strDesc, current);
        } else if (Jtype.Primitive.ARRAY.hasCode(code)) {
            return resolveArray(strDesc, current);
        } else {
            return new TypeAndSize(Jtype.Primitive.getTypeByCode(code), 1);
        }
    }

    private static TypeAndSize resolverRefType(String strDesc, int current) {
        for (int i = current + PREFIX_GAP; i < strDesc.length(); i++) {
            if (strDesc.charAt(i) == ';') {
                var className = strDesc.substring(current + PREFIX_GAP, i);

                return new TypeAndSize(new Jtype.Reference(className), i + 1);
            }
        }
        throw new IllegalStateException("Can not able to parse type. Internal error with ref/arr type %s".formatted(strDesc));
    }

    private static TypeAndSize resolveArray(String strDesc, int current) {
        TypeAndSize typeAndSize = resolveTypeAndSize(strDesc, current + 1);

        Jtype subtype = typeAndSize.type();

        if (subtype instanceof Jtype.Primitive p && (p == Jtype.Primitive.REFERENCE || p == Jtype.Primitive.VOID)) {
            throw new IllegalStateException("Incompatible type %s for array".formatted(subtype));
        }

        return new TypeAndSize(new Jtype.Array(subtype), typeAndSize.size + 1);
    }

    public record TypeAndSize(Jtype type, int size) {
    }
}
