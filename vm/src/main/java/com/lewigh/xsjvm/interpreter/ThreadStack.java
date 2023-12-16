package com.lewigh.xsjvm.interpreter;

import com.lewigh.xsjvm.VmException;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

        public static Exception create(ThreadStack stack, java.lang.Exception e) {

            ArrayList<StackFrame> stackFrames = new ArrayList<>(stack.frames);

            StringBuilder sb = new StringBuilder();

            for (var f : stackFrames) {
                sb.append("  %s.%s()".formatted(f.getKlass().name(), f.getMethod().name()));
                sb.append("\n");
            }

            return new Exception(
                    "method call chain:%n%s".formatted(sb.toString()),
                    e);
        }
    }
}
