package com.lewigh.xsjvm;

import com.lewigh.xsjvm.classloader.AppClassLoader;
import com.lewigh.xsjvm.classloader.ClassStorage;
import com.lewigh.xsjvm.gc.*;
import com.lewigh.xsjvm.engine.ExecutionEngine;
import com.lewigh.xsjvm.classloader.reader.ClassReader;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            throw new VmException("Cannot start VM. There is no path to RT classes in the VM args");
        }

        start(Path.of(args[0]), Path.of("./sampler/target/classes"), "org/gdiff/xsjvm/sampler/Sample");
    }

    private static void start(Path rtClassesPath, Path appClassesPath, String mainClass) {
        try {
            MemoryManager memManager = new UnsafeMemoryManager(ByteBufferMemoryAllocator.create(1024 * 1000));
            ClassReader reader = new ClassReader();
            ClassStorage classStorage = new ClassStorage();

            try (Stream<Path> systemPathStream = resolveClassPath(rtClassesPath);
                 Stream<Path> appPathStream = resolveClassPath(appClassesPath)) {

                var classPath = Stream.concat(systemPathStream, appPathStream).toList();

                var classLoader = new AppClassLoader(classPath, reader, classStorage);
                var engine = new ExecutionEngine(classLoader, memManager);

                engine.execute(mainClass);
            }
        } catch (IOException | MemoryManagmentException e) {
            throw new VmException(e);
        }
    }

    private static Stream<Path> resolveClassPath(@NonNull Path source) throws IOException {
        return Files.find(source, 20, (p, a) -> a.isRegularFile());
    }

}
