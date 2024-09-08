package com.lewigh.xsjvm.classloader;

import com.lewigh.xsjvm.classloader.reader.info.attribute.*;
import com.lewigh.xsjvm.engine.runtime.*;
import com.lewigh.xsjvm.classloader.reader.ClassReader;
import com.lewigh.xsjvm.classloader.reader.info.ClassFile;
import com.lewigh.xsjvm.classloader.reader.info.FieldInfo;
import com.lewigh.xsjvm.classloader.reader.info.MethodInfo;

import java.nio.file.Path;
import java.util.*;

import static com.lewigh.xsjvm.classloader.reader.resolvers.DescriptorResolver.resolveMethodDescriptor;
import static com.lewigh.xsjvm.classloader.reader.resolvers.DescriptorResolver.resolveType;
import static com.lewigh.xsjvm.engine.runtime.Access.*;
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


    public KlassDesc load(String className) {
        return requireNonNullElseGet(
                classStorage.getByName(className),
                () -> executeLoading(className));
    }

    private KlassDesc executeLoading(String className) {

        System.out.printf("  Loading %s%n", className);

        String path = className
                .replace('.', '/')
                .concat(".class");

        var classFullPath = classPath.stream()
                .filter(a -> a.endsWith(path))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to load the class %s".formatted(className)));


        var classFile = reader.read(classFullPath.toString());

        KlassDesc superClass = resolveSuper(classFile);
        KlassDesc[] interfaces = resolveInterfaces(classFile);
        HashMap<String, MethodDesc> methods = new HashMap<>();
        HashMap<String, MethodDesc> vtable = new HashMap<>();

        FieldDescGroup fields = resolveFields(classFile, superClass);

        resolveMethods(classFile, superClass, methods, vtable);

        KlassDesc newklass = new KlassDesc(
                classStorage.nextId(),
                classFile.thisName(),
                superClass,
                classFile.accessFlags(),
                interfaces,
                fields,
                methods,
                vtable,
                classFile.constantPool(),
                new KlassDesc.State()
        );

        classStorage.store(newklass);

        return newklass;
    }

    private KlassDesc resolveSuper(ClassFile classFile) {
        return classFile.superName() != null ? load(classFile.superName()) : null;
    }

    private KlassDesc[] resolveInterfaces(ClassFile classFile) {

        String[] interfacesNames = classFile.interfaces();
        KlassDesc[] interfaces = new KlassDesc[interfacesNames.length];

        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = load(interfacesNames[0]);
        }

        return interfaces;
    }

    private FieldDescGroup resolveFields(ClassFile classFile, KlassDesc superClass) {
        FieldInfo[] infoFields = classFile.fields();
        LinkedHashMap<String, FieldDesc> kFields = new LinkedHashMap<>(infoFields.length);

        long instanceOffset = 0;
        long staticOffset = 0;

        if (superClass != null) {

            instanceOffset += superClass.fieldGroup().instanceSize();
            staticOffset += superClass.fieldGroup().staticSize();

            LinkedHashMap<String, FieldDesc> superFields = superClass.fieldGroup().fields();

            kFields.putAll(superFields);
        }

        for (var info : infoFields) {

            var typeAndIdx = resolveType(info.descriptor());

            FieldDesc field = new FieldDesc(
                    info.name(),
                    typeAndIdx,
                    computeAccess(info),
                    info.isStatic(),
                    info.isFinal(),
                    info.isVolatile(),
                    info.isTransient(),
                    info.isSynthetic(),
                    info.isEnum(),
                    info.isStatic() ? staticOffset : instanceOffset
            );

            kFields.put(classFile.thisName() + "." + info.name(), field);

            Jtype type = field.type();

            byte fieldsSize = type
                    .primitive()
                    .getAlign()
                    .getTotal();

            if (info.isStatic()) {
                staticOffset += fieldsSize;
            } else {
                instanceOffset += fieldsSize;
            }
        }

        return new FieldDescGroup(kFields, instanceOffset, staticOffset);
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


    private void resolveMethods(ClassFile classFile, KlassDesc superClass, Map<String, MethodDesc> methods, Map<String, MethodDesc> virtuals) {

        for (var methodInfo : classFile.methods()) {
            var mName = methodInfo.name();

            if (methodInfo.isNative()) {
                MethodDesc nativeMethod = new MethodDesc(
                        mName,
                        resolveMethodDescriptor(methodInfo.descriptor()),
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
                        new ExceptionTable[0],
                        new HashMap<>()

                );

                methods.put("%s%s".formatted(nativeMethod.name(), methodInfo.descriptor()), nativeMethod);
            }

            AttributeInfo[] attributes = methodInfo.attributes();

            HashMap<Short, Short> lineMapping = new HashMap<>();

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

            for (var codeAt : codeAtt.attributes()) {
                if (codeAt instanceof LineNumberTableAttribute la) {
                    for (var entry : la.lineNumberTable()) {
                        lineMapping.put(entry.startPc(), entry.lineNumber());
                    }
                }
            }


            Access access = computeAccess(methodInfo);

            MethodDesc methodMeta = new MethodDesc(
                    mName,
                    resolveMethodDescriptor(methodInfo.descriptor()),
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
                    codeAtt.exceptionTable(),
                    lineMapping
            );

            methods.put("%s%s".formatted(methodMeta.name(), methodInfo.descriptor()), methodMeta);
        }

        virtuals.putAll(methods);

        if (superClass != null) {
            Map<String, MethodDesc> superMethods = superClass.methods();

            for (var mEntry : superMethods.entrySet()) {
                String sName = mEntry.getKey();

                if (!virtuals.containsKey(sName)) {
                    MethodDesc superMethod = mEntry.getValue();

                    if (!superMethod.fFinal() && !superMethod.fStatic() && superMethod.access() != PRIVATE) {
                        virtuals.put(sName, superMethod);
                    }

                }
            }
        }

    }

}
