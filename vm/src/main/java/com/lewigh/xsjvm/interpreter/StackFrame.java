package com.lewigh.xsjvm.interpreter;

import com.lewigh.xsjvm.VmException;
import com.lewigh.xsjvm.interpreter.runtime.Klass;
import com.lewigh.xsjvm.interpreter.runtime.Method;
import com.lewigh.xsjvm.interpreter.runtime.Value;
import com.lewigh.xsjvm.reader.info.attribute.Instruction;
import com.lewigh.xsjvm.reader.info.attribute.OpCode;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import lombok.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class StackFrame {
    private final Klass klass;
    private final Method method;
    @Getter(AccessLevel.PRIVATE)
    private final Queue<Value> stack;
    @Getter(AccessLevel.PRIVATE)
    private final Value[] localTable;

    short ip = 0;

    public static StackFrame create(@NonNull Klass klass, @NonNull Method methodMeta) {
        Value[] locals = new Value[methodMeta.maxLocals()];

        return create(klass, methodMeta, locals);
    }

    public void push(@NonNull Value value) {
        stack.offer(value);
    }

    @NonNull
    public Value pop() {
        Value poll = stack.poll();

        if (poll == null) {
            throw Exception.create("Stack value can not be null", this);
        }

        return poll;
    }

    public void inc() {
        ip++;
    }

    public void goTo(short newIp) {
        int instructionLength = method.instructions().length;
        if (newIp >= instructionLength) {
            throw new VmException("Unable to go to specify instruction %d. Out of range %d".formatted(newIp, instructionLength - 1));
        }
        ip = newIp;
    }

    public void popAndStoreTo(int index) {
        localTable[index] = pop();
    }

    @NonNull
    public Value load(int idx) {
        return localTable[idx];
    }

    @NonNull
    public ConstantPool getPooll() {
        return klass.constantPool();
    }

    public StackFrame fork(@NonNull Klass klass, @NonNull Method method) {
        Value[] locals = new Value[method.maxLocals()];

        for (int i = locals.length - 1; i >= 0 && !stack.isEmpty(); i--) {
            locals[i] = this.pop();
        }

        return create(klass, method, locals);
    }


    private static StackFrame create(@NonNull Klass klass, @NonNull Method method, @NonNull Value[] locals) {
        return new StackFrame(
                klass,
                method,
                Collections.asLifoQueue(new ArrayDeque<>(method.maxStack())),
                locals
        );
    }

    public static class Exception extends VmException {
        private Exception() {
        }

        private Exception(String message) {
            super(message);
        }

        private Exception(String message, Throwable cause) {
            super(message, cause);
        }

        private Exception(Throwable cause) {
            super(cause);
        }

        public static Exception create(@NonNull String message, @NonNull StackFrame frame) {
            String klassName = frame.getKlass().name();
            String methodName = frame.getMethod().name();

            Instruction[] instructions = frame.getMethod().instructions();

            StringBuilder sb = new StringBuilder();

            int curIp = frame.getIp() - 1;

            for (int i = 0; i < instructions.length; i++) {
                if (i == curIp) {
                    sb.append("  ->");
                } else {
                    sb.append("    ");
                }
                OpCode opCode = instructions[i].opCode();

                sb.append(opCode);
                if (i < instructions.length - 1) {
                    sb.append('\n');
                }
            }

            String stackParams = frame.stack.stream()
                    .map(a -> a.toString())
                    .collect(Collectors.joining(", ", "", "  "));

            String locals = Arrays.stream(frame.localTable)
                    .filter(a -> a != null)
                    .map(a -> a.toString())
                    .collect(Collectors.joining(", ", "", "  "));

            String formatted = "%s Info:%n  class: %s%n  method: %s%n  stack: %s%n  locals: %s%n  instructions:%n%s"
                    .formatted(message, klassName, methodName, stackParams, locals, sb.toString());

            return new Exception(formatted);
        }
    }

}
