package com.lewigh.xsjvm.engine;

import com.lewigh.xsjvm.VmException;
import com.lewigh.xsjvm.engine.runtime.Jtype;
import com.lewigh.xsjvm.engine.runtime.KlassDesc;
import com.lewigh.xsjvm.engine.runtime.MethodDesc;
import com.lewigh.xsjvm.engine.runtime.Value;
import com.lewigh.xsjvm.classloader.reader.info.attribute.Instruction;
import com.lewigh.xsjvm.classloader.reader.info.attribute.OpCode;
import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;
import lombok.*;

import java.util.*;

import static java.util.stream.Collectors.joining;

@Data
@RequiredArgsConstructor
public class StackFrame {
    private final KlassDesc klass;
    private final MethodDesc method;
    @Getter(AccessLevel.PRIVATE)
    private final Queue<Value> stack;
    @Getter(AccessLevel.PRIVATE)
    private final Value[] localTable;
    private final int paramLen;

    short ip = 0;

    public static StackFrame create(@NonNull KlassDesc klass, @NonNull MethodDesc methodMeta) {
        Value[] locals = new Value[methodMeta.maxLocals()];

        return create(klass, methodMeta, locals, 0);
    }

    public void push(Value value) {
        if (value == null) {
            throw Exception.create("push operation can not consume null", this);
        }
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

    public Value.Int popInt() {
        Value popped = pop();
        if (popped instanceof Value.Int i) {
            return i;
        }
        throw Exception.create("Ошибка приведения типа %s к Int".formatted(popped), this);
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
    public ConstantPool getPool() {
        return klass.constantPool();
    }

    public Value[] getParameters() {
        Value[] params = new Value[paramLen];

        System.arraycopy(localTable, 0, params, 0, paramLen);

        return params;
    }

    public StackFrame fork(@NonNull KlassDesc klass, @NonNull MethodDesc methodDesc) {
        Value[] locals = new Value[methodDesc.maxLocals()];
        Jtype[] methodParamTypes = methodDesc.descriptor().paarameterTypes();

        int passed = 0;

        for (int i = locals.length - 1; i >= 0 && !stack.isEmpty(); i--) {
            locals[i] = this.pop();
            passed++;
        }

        if (passed < methodParamTypes.length) {
            throw StackFrame.Exception.create(
                    "Error while preraring method call arguments for method %s%s%nExpected %d arguments%n%s%nbut passed %d%n%s%n%n".formatted(
                            klass.name(),
                            methodDesc.name(),
                            methodParamTypes.length,
                            Arrays.stream(methodParamTypes).map(a -> " " + a.toString()).collect(joining("\n")),
                            passed,
                            Arrays.stream(locals).limit(passed).map(a -> " " + a.toString()).collect(joining("\n"))
                    ),
                    this);
        }

        return create(klass, methodDesc, locals, passed);
    }


    private static StackFrame create(@NonNull KlassDesc klass, @NonNull MethodDesc method, @NonNull Value[] locals, int paramsLen) {
        return new StackFrame(
                klass,
                method,
                Collections.asLifoQueue(new ArrayDeque<>(method.maxStack())),
                locals,
                paramsLen
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
                    .collect(joining(", ", "", "  "));

            StringJoiner joiner = new StringJoiner(", ", "", "  ");
            Value[] table = frame.localTable;
            for (int i = 0; i < table.length; i++) {
                Value a = table[i];
                if (a != null) {
                    String string = a.toString();
                    joiner.add("%d:%s".formatted(i, string));
                } else {
                    joiner.add("%d:null!".formatted(i));
                }
            }
            String locals = joiner.toString();

            String formatted = "%s Info:%n  class: %s%n  method: %s%n  stack: <|%s%n  locals: %s%n  instructions:%n%s"
                    .formatted(message, klassName, methodName, stackParams, locals, sb.toString());

            return new Exception(formatted);
        }
    }

}
