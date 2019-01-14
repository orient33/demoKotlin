package com.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MyClass {
    public static void main(String[] args) {
        //test RxJava
        RxJavaDemo.test();
//        GsonTest.test();
        log(String.format(Locale.CHINA, "%.2f,%.2f", 39.2222d, 116.3333d));
//        StringCodeUtil.test();
//        TimerTest.test();

//        try {
//            ProcessLogCount.count();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        List<String> strList = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        moveItem(6, 4, strList);
        log("after move: " + list2String(strList));
        log("run completed ! ");
    }

    public static <T> void moveItem(int sourceIndex, int targetIndex, List<T> list) {
        if (sourceIndex <= targetIndex) {
            Collections.rotate(list.subList(sourceIndex, targetIndex + 1), -1);
        } else {
            Collections.rotate(list.subList(targetIndex, sourceIndex + 1), 1);
        }
    }

    public static <T> String list2String(List<T> list) {
        if (list == null) {
            return "list.null";
        } else if (list.isEmpty()) {
            return "list.empty";
        } else {
            StringBuilder sb = new StringBuilder(list.hashCode() + ", list size(" + list.size() + "):");
            for (T t : list) {
                sb.append(t.toString()).append(",");
            }
            return sb.toString();
        }
    }

    static void log(String s) {
        System.out.println(s);
    }
}