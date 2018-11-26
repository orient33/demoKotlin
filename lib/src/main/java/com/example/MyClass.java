package com.example;

import java.util.Locale;

public class MyClass {
    public static void main(String[] args) {
        //test RxJava
        RxJavaDemo.test();
        log(String.format(Locale.CHINA, "%.2f,%.2f", 39.2222d, 116.3333d));
//        StringCodeUtil.test();
        log("run completed ! ");
    }

    private static void log(String s) {
        System.out.println(s);
    }
}