package com.example;

import java.util.regex.Pattern;

public class PatternTest {
    public static void test() {
        final Pattern pattern = Pattern.compile("widget_[1-9]x[1-9]");
        String widget = "widget_5x6";
        log("pattern = widget_[1-9]x[1-9] , value = widget_5x6");
        log(" match ?  " + pattern.matcher(widget).matches());
        String[] s = pattern.split(widget);
        if (s != null && s.length > 0) {
            for (int i = 0; i < s.length; ++i) {
                log(i + " split : " + s[0]);
            }
        }
    }


    private static void log(String s) {
        System.out.println("PatternTest: " + s);
    }
}
