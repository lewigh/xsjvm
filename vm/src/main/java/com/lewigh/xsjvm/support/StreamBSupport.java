package com.lewigh.xsjvm.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamBSupport {

    public static byte readAsByte(InputStream is) throws IOException {
        return ByteBuffer.wrap(is.readNBytes(1)).get(0);
    }

    public static short readAsShort(InputStream is) throws IOException {
        return ByteBuffer.wrap(is.readNBytes(2)).getShort();
    }

    public static int readAsInt(InputStream is) throws IOException {
        return ByteBuffer.wrap(is.readNBytes(4)).getInt();
    }

    public static long readAsLong(InputStream is) throws IOException {
        return ByteBuffer.wrap(is.readNBytes(8)).getInt();
    }

    public static float readAsFloat(InputStream is) throws IOException {
        return ByteBuffer.wrap(is.readNBytes(4)).getInt();
    }

    public static double readAsDouble(InputStream is) throws IOException {
        return ByteBuffer.wrap(is.readNBytes(8)).getInt();
    }

    public static String readAsString(InputStream is, short size) throws IOException {
        return new String(is.readNBytes(size));
    }
}
