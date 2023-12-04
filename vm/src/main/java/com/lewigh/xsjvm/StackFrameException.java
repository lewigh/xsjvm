package com.lewigh.xsjvm;

import com.lewigh.xsjvm.interpreter.runtime.Klass;
import com.lewigh.xsjvm.interpreter.runtime.Method;
import com.lewigh.xsjvm.reader.info.attribute.Instruction;
import com.lewigh.xsjvm.reader.info.attribute.OpCode;

public class StackFrameException extends VmException {
    public StackFrameException() {
    }

    public StackFrameException(String message) {
        super(message);
    }

    public StackFrameException(String message, Throwable cause) {
        super(message, cause);
    }

    public StackFrameException(Throwable cause) {
        super(cause);
    }

    public static StackFrameException create(String message, Klass klass, Method method, int ip) {
        String klassName = klass.name();
        String methodName = method.name();

        Instruction[] instructions = method.instructions();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < instructions.length; i++) {
            if (i == ip) {
                sb.append("->");
            } else {
                sb.append("  ");
            }
            OpCode opCode = instructions[i].opCode();

            sb.append(opCode);
            sb.append('\n');
        }

        String formatted = "%s Info:%nclass:%s%nmethod:%s%ninstruction:%s%n".formatted(message, klassName, methodName, sb.toString());

        return new StackFrameException(formatted);
    }
}
