package com.lewigh.xsjvm.classloader;

import com.lewigh.xsjvm.engine.runtime.Klass;

import java.util.HashMap;
import java.util.Map;

public class ClassStorage {
    private final Map<String, Klass> nameAndClasses = new HashMap<>();
    private final Map<Integer, Klass> idAndClass = new HashMap<>();

    private final Map<Integer, Long> staticTable = new HashMap<>();

    private int nextId = 0;


    public Klass getById(int classId) {
        return idAndClass.get(classId);
    }

    public Klass getByName(String className) {
        return nameAndClasses.get(className);
    }

    public int nextId() {
        nextId++;
        return nextId;
    }

    public void store(Klass klass) {
        idAndClass.put(klass.id(), klass);
        nameAndClasses.put(klass.name(), klass);
    }
}
