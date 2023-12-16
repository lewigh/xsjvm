package com.lewigh.xsjvm.classloader.reader.resolvers;


import com.lewigh.xsjvm.classloader.reader.pool.ConstantPool;

import java.io.IOException;
import java.io.InputStream;

import static com.lewigh.xsjvm.support.StreamBSupport.readAsShort;


public class InterfaceResolver {

    public static String[] resolve(InputStream is, ConstantPool constantPool) throws IOException {
        int len = readAsShort(is);
        var interfaces = new String[len];

        for (int i = 0; i < len; i++) {
            short interfaceRef = readAsShort(is);
            interfaces[i] = constantPool.resolveClassRef(interfaceRef);
        }
        return interfaces;
    }
}
