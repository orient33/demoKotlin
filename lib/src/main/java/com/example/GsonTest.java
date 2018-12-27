package com.example;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GsonTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Foo {
        // Field tag only annotation
    }

    public static class MyExclusionStrategy implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> clazz) {
            return false;//(clazz == typeToSkip);
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(Foo.class) != null;
        }
    }

    static void test() {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new MyExclusionStrategy())
                .create();
//                excludeFieldsWithoutExposeAnnotation().create();
        Data d = new Data();
        d.name = "data.name";
        d.last = "data.last";
        log("out: " + gson.toJson(d));
    }

    private static void log(String s) {
        System.out.println(s);
    }

    static class Data {
        public String name;
        @Foo
        public String last;
    }
}
