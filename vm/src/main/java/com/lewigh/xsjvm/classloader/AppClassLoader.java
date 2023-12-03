package com.lewigh.xsjvm.classloader;

import com.lewigh.xsjvm.interpreter.runtime.*;
import com.lewigh.xsjvm.reader.ClassReader;
import com.lewigh.xsjvm.reader.info.ClassFile;
import com.lewigh.xsjvm.reader.info.FieldInfo;
import com.lewigh.xsjvm.reader.info.MethodInfo;
import com.lewigh.xsjvm.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.reader.info.attribute.CodeAttribute;
import com.lewigh.xsjvm.reader.info.attribute.ExceptionTable;
import com.lewigh.xsjvm.reader.info.attribute.Instruction;
import lombok.NonNull;

import java.nio.file.Path;
import java.util.*;

import static com.lewigh.xsjvm.interpreter.runtime.Access.*;
import static java.util.Objects.requireNonNullElseGet;

public class AppClassLoader {

    private final List<Path> classPath;
    private final ClassReader reader;
    private final ClassStorage classStorage;

    public AppClassLoader(List<Path> classPath, ClassReader reader, ClassStorage classStorage) {
        this.classPath = classPath;
        this.reader = reader;
        this.classStorage = classStorage;
    }


    public Klass load(String className) {
        return requireNonNullElseGet(
                classStorage.getByName(className),
                () -> executeLoading(className));
    }

    private Klass executeLoading(String className) {
        String path = className
                .replace('.', '/')
                .concat(".class");

        var classFullPath = classPath.stream()
                .filter(a -> a.endsWith(path))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to load the class %s".formatted(className)));


        var classFile = reader.read(classFullPath.toString());

        Klass superClass = resolveSuper(classFile);
        Klass[] interfaces = resolveInterfaces(classFile);
        HashMap<String, Method> methods = new HashMap<>();
        HashMap<String, Method> vtable = new HashMap<>();

        FieldGroup fields = resolveFields(classFile, superClass);

        resolveMethods(classFile, superClass, methods, vtable);

        Klass newklass = new Klass(
                classStorage.nextId(),
                classFile.thisName(),
                superClass,
                classFile.accessFlags(),
                interfaces,
                fields,
                methods,
                vtable,
                classFile.constantPool(),
                new Klass.State()
        );

        classStorage.store(newklass);

        return newklass;
    }

    private Klass resolveSuper(ClassFile classFile) {
        return classFile.superName() != null ? load(classFile.superName()) : null;
    }

    private Klass[] resolveInterfaces(ClassFile classFile) {

        String[] interfacesNames = classFile.interfaces();
        Klass[] interfaces = new Klass[interfacesNames.length];

        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = load(interfacesNames[0]);
        }

        return interfaces;
    }

    private FieldGroup resolveFields(ClassFile classFile, Klass superClass) {
        FieldInfo[] fields = classFile.fields();
        LinkedHashMap<String, Field> kFields = new LinkedHashMap<>(fields.length);

        long instanceOffset = 0;
        long staticOffset = 0;

        if (superClass != null) {

            instanceOffset += superClass.fieldGroup().instanceSize();
            staticOffset += superClass.fieldGroup().staticSize();

            LinkedHashMap<String, Field> superFields = superClass.fieldGroup().fields();

            kFields.putAll(superFields);
        }

        for (var f : fields) {

            TypeAndIdx typeAndIdx = resolveType(f.descriptor(), 0);

            Field field = new Field(
                    f.name(),
                    typeAndIdx.type(),
                    computeAccess(f),
                    f.isStatic(),
                    f.isFinal(),
                    f.isVolatile(),
                    f.isTransient(),
                    f.isSynthetic(),
                    f.isEnum(),
                    f.isStatic() ? staticOffset : instanceOffset
            );

            kFields.put(classFile.thisName() + "." + f.name(), field);

            Jtype type = field.type();

            byte fieldsSize = type
                    .primitive()
                    .getAlign()
                    .getTotal();

            if (f.isStatic()) {
                staticOffset += fieldsSize;
            } else {
                instanceOffset += fieldsSize;
            }
        }

        return new FieldGroup(kFields, instanceOffset, staticOffset);
    }

    private Access computeAccess(FieldInfo field) {
        if (field.isPublic()) {
            return PUBLIC;
        } else if (field.isProtected()) {
            return PROTECTED;
        } else if (field.isPrivate()) {
            return PRIVATE;
        }
        return LOCAL;
    }

    private Access computeAccess(MethodInfo method) {
        if (method.isPublic()) {
            return PUBLIC;
        } else if (method.isProtected()) {
            return PROTECTED;
        } else if (method.isPrivate()) {
            return PRIVATE;
        }
        return LOCAL;
    }


    private void resolveMethods(ClassFile classFile, Klass superClass, Map<String, Method> methods, Map<String, Method> virtuals) {

        for (var methodInfo : classFile.methods()) {
            var mName = methodInfo.name();

            if (methodInfo.isNative()) {
                Method nativeMethod = new Method(
                        mName,
                        null,
                        computeAccess(methodInfo),
                        methodInfo.isStatic(),
                        methodInfo.isFinal(),
                        true,
                        methodInfo.isSynchronized(),
                        false,
                        methodInfo.isVarargs(),
                        false,
                        false,
                        methodInfo.isStrict(),
                        (short) 0,
                        (short) 0,
                        new Instruction[0],
                        new ExceptionTable[0]

                );

                methods.put("%s%s".formatted(nativeMethod.name(), methodInfo.descriptor()), nativeMethod);
            }

            AttributeInfo[] attributes = methodInfo.attributes();


            CodeAttribute codeAtt = Arrays.stream(attributes)
                    .map(a -> {
                        if (a instanceof CodeAttribute c) {
                            return c;
                        }
                        return null;
                    })
                    .filter(a -> a != null)
                    .findFirst().orElse(null);

            if (codeAtt == null) {
                continue;
            }

            Access access = computeAccess(methodInfo);

            Method methodMeta = new Method(
                    mName,
                    null,
                    access,
                    methodInfo.isStatic(),
                    methodInfo.isFinal(),
                    methodInfo.isNative(),
                    methodInfo.isSynchronized(),
                    methodInfo.isAbstract(),
                    methodInfo.isVarargs(),
                    methodInfo.isBridge(),
                    methodInfo.isSynthetic(),
                    methodInfo.isStrict(),
                    codeAtt.maxStack(),
                    codeAtt.maxLocals(),
                    codeAtt.code(),
                    codeAtt.exceptionTable()
            );

            methods.put("%s%s".formatted(methodMeta.name(), methodInfo.descriptor()), methodMeta);
        }

        virtuals.putAll(methods);

        if (superClass != null) {
            Map<String, Method> superMethods = superClass.methods();

            for (var mEntry : superMethods.entrySet()) {
                String sName = mEntry.getKey();

                if (!virtuals.containsKey(sName)) {
                    Method superMethod = mEntry.getValue();

                    if (!superMethod.fFinal() && !superMethod.fStatic() && superMethod.access() != PRIVATE) {
                        virtuals.put(sName, superMethod);
                    }

                }
            }
        }

    }

    private MethodDescriptor resolveMethodDescriptor(String strDesc) {

        ArrayList<Jtype> types = new ArrayList<>();

        int current = 0;

        while (current < strDesc.length() - 1) {
            var typeAndGap = resolveType(strDesc, current);

            types.add(typeAndGap.type());

            current += typeAndGap.idx();

        }

        Jtype retType = types.remove(types.size() - 1);

        Jtype[] parameters = new Jtype[types.size()];

        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = types.get(i);
        }

        return new MethodDescriptor(
                parameters,
                retType
        );
    }

    @NonNull
    private TypeAndIdx resolveType(String strDesc, int current) {
        char code = strDesc.charAt(current);

        boolean refType = Jtype.Primitive.REFERENCE.hasCode(code);
        boolean arrType = Jtype.Primitive.ARRAY.hasCode(code);

        if (refType || arrType) {
            for (int i = current + 1; i < strDesc.length(); i++) {
                if (strDesc.charAt(i) == ';') {
                    var className = strDesc.substring(current + 1, i);

                    Jtype jtype = refType
                            ? new Jtype.Reference(className)
                            : new Jtype.Array(resolveType(className, 0).type());

                    return new TypeAndIdx(jtype, i);
                }
            }
        }
        Jtype.Primitive type = Jtype.Primitive.getTypeByCode(code);
        return new TypeAndIdx(type, current);
    }

    record TypeAndIdx(Jtype type, int idx) {
    }
}
