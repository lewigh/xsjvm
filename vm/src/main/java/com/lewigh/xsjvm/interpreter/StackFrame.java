package com.lewigh.xsjvm.interpreter;

import com.lewigh.xsjvm.VmException;
import com.lewigh.xsjvm.interpreter.runtime.Value;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import com.lewigh.xsjvm.interpreter.runtime.Klass;
import com.lewigh.xsjvm.interpreter.runtime.Method;
import lombok.*;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

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

//        if (poll == null) {
//            throw new VmException("");
//        }

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
    public ConstantPool getPoll() {
        return klass.constantPool();
    }

    public StackFrame fork(@NonNull Klass klass, @NonNull Method method) {
        Value[] locals = new Value[method.maxLocals()];

        // FIXME IS A PROBLEM PLACE AS THERE MAY BE MORE PARAMETERS FOR LOCAL VARIABLES
        for (int i = locals.length - 1; i >= 0; i--) {
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

}
