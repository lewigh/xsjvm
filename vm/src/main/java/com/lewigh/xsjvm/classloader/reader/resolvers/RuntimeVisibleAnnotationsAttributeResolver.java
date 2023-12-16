package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.info.attribute.ElementValue;
import com.lewigh.xsjvm.classloader.reader.info.attribute.RuntimeVisibleAnnotationsAttribute;
import com.lewigh.xsjvm.classloader.reader.info.attribute.TypeTag;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

class RuntimeVisibleAnnotationsAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {

        short numAnnotations = readAsShort(is);

        var annotations = new RuntimeVisibleAnnotationsAttribute.Annotation[numAnnotations];

        for (int i = 0; i < annotations.length; i++) {
            annotations[i] = resolveAnnotation(is);
        }

        return new RuntimeVisibleAnnotationsAttribute(
                attName,
                attLen,
                numAnnotations,
                annotations
        );
    }

    @Override
    public boolean supports(String name) {
        return name.equals(Attributes.RUNTIME_VISIBLE_ANNOTATIONS);
    }

    private RuntimeVisibleAnnotationsAttribute.Annotation resolveAnnotation(InputStream is) throws IOException {
        short typeIndex = readAsShort(is);
        short numElementValuePairs = readAsShort(is);

        var elementValuePairs = new RuntimeVisibleAnnotationsAttribute.Annotation.ElementValuePairs[numElementValuePairs];

        for (int j = 0; j < elementValuePairs.length; j++) {

            short elementNameIndex = readAsShort(is);
            int tag = is.read();

            ElementValue elementValue = resolveElementValue((char) tag, is);

            var pair = new RuntimeVisibleAnnotationsAttribute.Annotation.ElementValuePairs(elementNameIndex, elementValue);

            elementValuePairs[j] = pair;
        }

        return new RuntimeVisibleAnnotationsAttribute.Annotation(
                typeIndex,
                numElementValuePairs,
                elementValuePairs
        );
    }

    private ElementValue resolveElementValue(char cTag, InputStream is) throws IOException {
        var tag = TypeTag.getByTag(cTag);

        return switch (tag) {
            case T_BYTE, T_CHAR, T_DOUBLE, T_FLOAT, T_INT, T_LONG, T_SHORT, T_BOOLEAN, T_STRING -> new ElementValue.ConstValue(tag, readAsShort(is));
            case T_ENUM_CLASS -> new ElementValue.EnumConstValue(tag, readAsShort(is), readAsShort(is));
            case T_CLASS -> new ElementValue.ClassInfo(tag, readAsShort(is));
            case T_ANNOTATION_INTERFACE -> new ElementValue.Annotation(tag, resolveAnnotation(is));
            case T_ARRAY_TYPE -> resolveArrayType(tag, is);
        };
    }

    private ElementValue.ArrayValue resolveArrayType(TypeTag tag, InputStream is) throws IOException {
        short numValues = readAsShort(is);

        var elementValues = new ElementValue[numValues];

        for (int i = 0; i < elementValues.length; i++) {
            int iTag = is.read();

            ElementValue internalElementValue = resolveElementValue((char) iTag, is);

            elementValues[i] = internalElementValue;
        }

        return new ElementValue.ArrayValue(tag, numValues, elementValues);
    }
}
