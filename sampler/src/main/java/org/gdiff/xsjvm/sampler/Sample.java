package org.gdiff.xsjvm.sampler;

import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings("ManualMinMaxCalculation")
public class Sample {
    public static void main(String[] args) {

        int x = 888888888;
        String hello = "Hello";

//        System.out.println(hello);

        int result = add(1, 2);

        Point point = new Point(10, 20);

        int max = max(1, 2);

        int z = Point.getZ();

        workWithArray();

        float v = tryCatch();

//        Point p = new Point(3, 4);
        Obj aObj = new Obj();
        boolean inst = aObj instanceof D;

//        boolean inst = p instanceof D;
//        throwE();
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public static void workWithArray() {
        int[] arr = new int[10];
        arr[0] = 1;

        int res = arr[0];

        int length = arr.length;
    }

    public static float tryCatch() {
        try {
            return (float) 10 / 5;
        } catch (ArithmeticException e) {
            throw new IllegalStateException(e);
        } finally {

        }
    }

    public static void throwE() {
        throw new IllegalStateException();
    }
}
