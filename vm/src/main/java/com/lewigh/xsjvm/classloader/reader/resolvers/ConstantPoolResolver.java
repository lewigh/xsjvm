package com.lewigh.xsjvm.classloader.reader.resolvers;


import com.lewigh.xsjvm.classloader.reader.pool.Constant;
import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.lewigh.xsjvm.support.StreamBSupport.*;
import static java.lang.String.format;

@SuppressWarnings({"java:S115", "SpellCheckingInspection"})
public class ConstantPoolResolver {

    private static final byte CONSTANT_Utf8 = 1;
    private static final byte CONSTANT_Integer = 3;
    private static final byte CONSTANT_Float = 4;
    private static final byte CONSTANT_Long = 5;
    private static final byte CONSTANT_Double = 6;
    private static final byte CONSTANT_Class = 7;
    private static final byte CONSTANT_String = 8;
    private static final byte CONSTANT_Fieldref = 9;
    private static final byte CONSTANT_Methodref = 10;
    private static final byte CONSTANT_InterfaceMethodref = 11;
    private static final byte CONSTANT_NameAndType = 12;
    private static final byte CONSTANT_MethodHandle = 15;
    private static final byte CONSTANT_MethodType = 16;
    private static final byte CONSTANT_Dynamic = 17;
    private static final byte CONSTANT_InvokeDynamic = 18;
    private static final byte CONSTANT_Module = 19;
    private static final byte CONSTANT_Package = 20;

    private ConstantPoolResolver() {
    }

    public static ConstantPool resolve(InputStream is) throws IOException {
        short constPoolLen = readAsShort(is);


        ArrayList<Constant> constants = new ArrayList<>();


        var constantPool = new ConstantPool(constants);

        constants.add((short) 0, null);

        boolean gap = false;

        for (short i = 1; i < constPoolLen; i++) {

            if (gap) {
                gap = false;
                constants.add(i, null);
                continue;
            }

            var tag = is.read();

            switch (tag) {
                case CONSTANT_Utf8 -> {
                    short textSize = readAsShort(is);
                    constants.add(i, new Constant.Utf8(readAsString(is, textSize)));
                }
                case CONSTANT_Integer -> constants.add(i, new Constant.ConstantInteger(readAsInt(is)));
                case CONSTANT_Float -> constants.add(i, new Constant.ConstantFloat(readAsFloat(is)));
                case CONSTANT_Long -> {
                    constants.add(i, new Constant.ConstantLong(readAsLong(is)));
                    gap = true;
                }
                case CONSTANT_Double -> {
                    constants.add(i, new Constant.ConstantDouble(readAsDouble(is)));
                    gap = true;
                }
                case CONSTANT_Class -> constants.add(i, new Constant.Class(readAsShort(is)));
                case CONSTANT_String -> constants.add(i, new Constant.ConstantStringRef(readAsShort(is)));
                case CONSTANT_Fieldref -> constants.add(i, new Constant.FieldInfo(readAsShort(is), readAsShort(is)));
                case CONSTANT_Methodref -> constants.add(i, new Constant.MethodRefInfo(readAsShort(is), readAsShort(is)));
                case CONSTANT_InterfaceMethodref -> constants.add(i, new Constant.InterfaceMethodRef(readAsShort(is), readAsShort(is)));
                case CONSTANT_NameAndType -> constants.add(i, new Constant.NameAndTypeInfo(readAsShort(is), readAsShort(is)));
                case CONSTANT_MethodHandle -> constants.add(i, new Constant.MethodHandleInfo(readAsByte(is), readAsShort(is)));
                case CONSTANT_MethodType -> constants.add(i, new Constant.MethodTypeInfo(readAsShort(is)));
                case CONSTANT_Dynamic -> constants.add(i, new Constant.DynamicInfo(readAsShort(is), readAsShort(is)));
                case CONSTANT_InvokeDynamic -> constants.add(i, new Constant.InvokeDynamicInfo(readAsShort(is), readAsShort(is)));
                case CONSTANT_Module -> constants.add(i, new Constant.ModuleInfo(readAsShort(is)));
                case CONSTANT_Package -> constants.add(i, new Constant.PackageInfo(readAsShort(is)));
                default -> throw new IllegalStateException(format("Tag %s not recognized", tag));
            }
        }
        return constantPool;
    }
}
