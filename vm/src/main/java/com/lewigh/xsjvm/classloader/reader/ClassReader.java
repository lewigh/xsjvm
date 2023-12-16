package com.lewigh.xsjvm.classloader.reader;

import com.lewigh.xsjvm.classloader.reader.info.attribute.AttributeInfo;
import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import com.lewigh.xsjvm.classloader.reader.resolvers.ConstantPoolResolver;
import com.lewigh.xsjvm.classloader.reader.resolvers.FieldResolver;
import com.lewigh.xsjvm.classloader.reader.resolvers.InterfaceResolver;
import com.lewigh.xsjvm.classloader.reader.resolvers.MethodResolver;
import com.lewigh.xsjvm.classloader.reader.flag.ClassAccessFlag;
import com.lewigh.xsjvm.classloader.reader.info.ClassFile;
import com.lewigh.xsjvm.classloader.reader.info.FieldInfo;
import com.lewigh.xsjvm.classloader.reader.info.MethodInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsInt;
import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class ClassReader {

    public ClassFile read(String className) {
        try (FileInputStream is = new FileInputStream(className)) {
            if (readAsInt(is) != 0xCAFEBABE) {
                throw new IllegalArgumentException("This is not a class-file");
            }

            short major = readAsShort(is);
            short minor = readAsShort(is);

            ConstantPool constantPool = ConstantPoolResolver.resolve(is);

            var accessFlags = ClassAccessFlag.parse(readAsShort(is));

            short classNameId = readAsShort(is);
            String thisName = constantPool.resolveClassRef(classNameId);
            short classNameIndex = readAsShort(is);
            var superClassName = retriveSuperName(thisName, constantPool, classNameIndex);

            String[] interfaces = InterfaceResolver.resolve(is, constantPool);
            FieldInfo[] fields = FieldResolver.resolve(is, constantPool, className);
            MethodInfo[] methods = MethodResolver.resolve(is, constantPool, className);

            return new ClassFile(
                    major,
                    minor,
                    accessFlags,
                    constantPool,
                    thisName,
                    superClassName,
                    interfaces,
                    fields,
                    methods,
                    new AttributeInfo[0]
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("An error occurred while loading %s class into memory".formatted(className), e);
        }
    }

    private static String retriveSuperName(String thisName, ConstantPool constantPool, short classNameIndex) {
        return thisName.equals("java/lang/Object")
                ? null
                : constantPool.resolveClassRef(classNameIndex);
    }
}
