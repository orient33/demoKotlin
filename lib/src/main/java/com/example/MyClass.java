package com.example;

import java.io.FileNotFoundException;
import java.util.Locale;

public class MyClass {
    public static void main(String[] args) {
        //test RxJava
//        RxJavaDemo.test();
//        GsonTest.test();
        log(String.format(Locale.CHINA, "%.2f,%.2f", 39.2222d, 116.3333d));
//        StringCodeUtil.test();
//        TimerTest.test();

//        try {
//            ProcessLogCount.count();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        log("run completed ! ");
    }

    static void log(String s) {
        System.out.println(s);
    }
}