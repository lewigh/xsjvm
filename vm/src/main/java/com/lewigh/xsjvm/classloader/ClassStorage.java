package com.lewigh.xsjvm.classloader;

import com.lewigh.xsjvm.engine.runtime.KlassDesc;

import java.util.HashMap;
import java.util.Map;

public class ClassStorage {
    private final Map<String, KlassDesc> nameAndClasses = new HashMap<>();
    private final Map<Integer, KlassDesc> idAndClass = new HashMap<>();

    private final Map<Integer, Long> staticTable = new HashMap<>();

    private int nextId = 0;


    public KlassDesc getById(int classId) {
        return idAndClass.get(classId);
    }

    public KlassDesc getByName(String className) {
        return nameAndClasses.get(className);
    }

    public int nextId() {
        nextId++;
        return nextId;
    }

    public void store(KlassDesc klass) {
        idAndClass.put(klass.id(), klass);
        nameAndClasses.put(klass.name(), klass);
    }
}
