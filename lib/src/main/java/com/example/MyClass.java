package com.example;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyClass {
    public static void main(String[] args) {
        //test RxJava
//        RxJavaDemo.test();
//        GsonTest.test();
        log("1小时等于 多少 毫秒ms"+ TimeUnit.HOURS.toMillis(1));
        log(String.format(Locale.CHINA, "%.2f,%.2f", 39.2222d, 116.3333d));
//        StringCodeUtil.test();
//        TimerTest.test();

        try {
            ProcessLogCount2.count();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        List<String> strList = Arrays.asList("0a", "1b", "2c", "3d", "4e", "5f", "6g");
//        Collections.swap(strList, 6, 4);
//        moveItem(6, 4, strList);
//        log("after move: " + list2String(strList));
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