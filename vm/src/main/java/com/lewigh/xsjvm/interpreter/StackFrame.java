package com.lewigh.xsjvm.interpreter;

import com.lewigh.xsjvm.interpreter.runtime.Value;
import com.lewigh.xsjvm.reader.pool.ConstantPool;
import com.lewigh.xsjvm.interpreter.runtime.Klass;
import com.lewigh.xsjvm.interpreter.runtime.Method;
import lombok.*;

import java.util.Queue;

@Data
@RequiredArgsConstructor
public class StackFrame {
    private final String className;
    private final String methodName;
    private final Klass klass;
    private final Method method;
    @Getter(AccessLevel.PRIVATE)
    private final Queue<Value> stack;
    @Getter(AccessLevel.PRIVATE)
    private final Value[] localTable;
    private final ConstantPool constantPool;

    short ip = 0;

    public void push(@NonNull Value value) {
        stack.offer(value);
    }

    @NonNull
    public Value pop() {
        return stack.poll();
    }

    public void inc() {
        ip++;
    }

    public void goTo(short newIp) {
        ip = newIp;
    }

    public void storeLocal(int index) {
        localTable[index] = pop();
    }

    @NonNull
    public Value getLocal(int idx) {
        return localTable[idx];
    }
}
