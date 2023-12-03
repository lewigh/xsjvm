package com.lewigh.xsjvm.support;

import com.lewigh.xsjvm.interpreter.StackFrame;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logger {

    public static void invoke(StackFrame frame) {
        Logger.debug("Invoke    %s.%s()%n", frame.getClassName(), frame.getMethodName());
    }

    public static void retval(StackFrame frame, Object retVal) {
        Logger.debug("Return     %s%s()-> %s%n", " ".repeat(frame.getClassName().length()), frame.getMethodName(), retVal);
    }

    public static void ret(StackFrame frame) {
        Logger.debug("Return     %s%s()%n", " ".repeat(frame.getClassName().length()), frame.getMethodName());
    }

    public static void debug(String text, Object... args) {
        System.out.printf(text, args);
    }
}
