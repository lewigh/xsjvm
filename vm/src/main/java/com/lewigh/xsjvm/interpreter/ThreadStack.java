package com.lewigh.xsjvm.interpreter;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

public class ThreadStack {
    private final Queue<StackFrame> frames;

    public ThreadStack() {
        this.frames = Collections.asLifoQueue(new ArrayDeque<>());
    }

    public StackFrame top() {
        return frames.peek();
    }

    public StackFrame pop() {
        return frames.poll();
    }

    public void push(StackFrame frame) {
        frames.offer(frame);
    }

    public boolean isEmpty() {
        return frames.isEmpty();
    }

    public int size() {
        return frames.size();
    }
}
