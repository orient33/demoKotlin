package com.example;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.MyClass.log;

class TimerTest {
    static int count;

    static void test() {
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                log("timer run: " + new Date());
                if (++count > 10) {
                    t.cancel();
                }
            }
        };

        t.schedule(tt, 0, 3000);
    }
}
