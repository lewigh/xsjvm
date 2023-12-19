package com.lewigh.xsjvm.classloader.reader.resolvers;

import com.lewigh.xsjvm.classloader.reader.info.attribute.*;
import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsInt;
import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;

public class CodeAttributeResolver implements AttributeResolvingStrategy {

    @NonNull
    @Override
    public AttributeInfo resolve(InputStream is, String attName, int attLen, ConstantPool pool, String className, String target) throws IOException {
        try {

            short maxStack = readAsShort(is);
            short maxLocals = readAsShort(is);
            int codeLength = readAsInt(is);
            byte[] codeBytes = is.readNBytes(codeLength);
            short exceptionTableLength = readAsShort(is);

            ExceptionTable[] exceptionTables = resolveExceptionTable(exceptionTableLength, is);

            short subAttributesCount = readAsShort(is);
            var subAttributes = new AttributeInfo[0];
            if (subAttributesCount > 0) {
                subAttributes = AttributeResolver.resolve(is, subAttributesCount, pool, className, target);
            }

            return new CodeAttribute(
                    attName,
                    attLen,
                    maxStack,
                    maxLocals,
                    codeLength,
                    resolveOperations(codeBytes),
                    exceptionTableLength,
                    exceptionTables,
                    subAttributesCount,
                    subAttributes
            );
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public boolean supports(@NonNull String name) {
        return name.equals(Attributes.CODE_ATT);
    }

    private static Instruction[] resolveOperations(byte[] bytes) {
        Instruction[] operations = new Instruction[bytes.length];

        int skip = 0;
        int realIdx = 0;

        for (int i = 0; i < bytes.length; i++) {
            if (skip > 0) {
                skip--;
                continue;
            }

            int unsignedInt = Byte.toUnsignedInt(bytes[i]);
            var opCode = OpCode.byCode(unsignedInt);
            int operandsBytesLen = opCode.getOperandsType().getBytesLen();
            byte[] operandsBuffer = new byte[operandsBytesLen];

            if (operandsBytesLen > 0) {
                for (int j = 0; j < operandsBytesLen; j++) {
                    operandsBuffer[j] = bytes[i + j + 1];
                    skip++;
                }
            }

            operations[realIdx] = new Instruction(opCode, opCode.getOperandsType().retrieve(operandsBuffer));
            realIdx++;
        }

        return Arrays.copyOf(operations, realIdx);

    }

    private static ExceptionTable[] resolveExceptionTable(int len, InputStream is) throws IOException {
        var exceptionTable = new ExceptionTable[len];

        for (int i = 0; i < len; i++) {

            short startPc = readAsShort(is);
            short endPc = readAsShort(is);
            short handlerPc = readAsShort(is);
            short catchType = readAsShort(is);

            exceptionTable[i] = new ExceptionTable(startPc, endPc, handlerPc, catchType);
        }

        return exceptionTable;
    }
}
