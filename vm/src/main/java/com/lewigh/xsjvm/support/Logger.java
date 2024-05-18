package com.lewigh.xsjvm.support;

import com.lewigh.xsjvm.engine.StackFrame;
import com.lewigh.xsjvm.engine.runtime.Value;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.StringJoiner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logger {

    public static void invoke(StackFrame frame) {
        String className = frame.getKlass().name();
        String metodName = frame.getMethod().name();
        Value[] parameters = frame.getParameters();
        var params = paramsAsString(parameters);
        Logger.debug("Invoke    %s.%s(%s)%n", className, metodName, params);
    }

    public static void retval(StackFrame frame, Object retVal) {
        Logger.debug("Return     %s%s()-> %s%n", " ".repeat(frame.getKlass().name().length()), frame.getMethod().name(), retVal);
    }

    public static void ret(StackFrame frame) {
        Logger.debug("Return     %s%s()%n", " ".repeat(frame.getKlass().name().length()), frame.getMethod().name());
    }

    public static void debug(String text, Object... args) {
        System.out.printf(text, args);
    }

    private static String paramsAsString(Value[] parameters) {
        if (parameters.length == 0) {
            return "";
        }
        StringJoiner sj = new StringJoiner(", ");
        for (var p : parameters) {
            sj.add(p.toString());
        }
        return sj.toString();
    }
}
