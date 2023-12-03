package org.gdiff.xsjvm.sampler;

public class Point {

    static int z = 56;

    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static int getZ() {
        return z;
    }
}
