package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.example.MyClass.log;

public class NioDemo {
    static void test(){
        Path path = Paths.get("/home/mi/downl//111.tgz");
        log("path is " + path);
        log("after normalize: " + path.normalize());
        Path targe = Paths.get("/home/mi/downl/11112.tgz");
        long start = System.currentTimeMillis();
        try {
            Files.copy(path, targe, StandardCopyOption.REPLACE_EXISTING);
            log("copy over: use. " + (System.currentTimeMillis() - start) + " ms ");
        } catch (IOException e) {
            log("copy e: " + e);
            e.printStackTrace();
        }
    }
}
