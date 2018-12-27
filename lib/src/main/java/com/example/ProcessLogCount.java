package com.example;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.example.MyClass.log;

/**
 * 因log太多,导致无法查看,分析需要的log. (log格式在最下面的注释)
 * 这里统计下每个进程的log行数,以便查看 到底哪个process再频繁打log.
 * 然后可以将此进程的app删除.
 */
class ProcessLogCount {
    static void count() throws FileNotFoundException {
        File file = new File("D:\\log.log");
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<Integer, Integer> map = new HashMap<>(100);
        int pid;
        int index;
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                if (line.length() < 31) {
                    if (line.startsWith("--------")) continue;
                    log("line too short : " + line);
                    continue;
                }
                line = line.substring(0, 30);
                String sub[] = line.split(" ");
                if (sub.length > 2) {
                    index = 2;
                    String v;
                    do {
                        v = sub[index++];
                    }
                    while (v.length() < 1 && index < sub.length);
                    if (v.length() < 1) continue;
                    pid = Integer.valueOf(v);
                    map.merge(pid, 1, (a, b) -> a + b);
                }
            }
            map.forEach((integer, integer2) -> {
                if (integer2 > 1000) {
                    log("Process : " + integer + ", count :" + integer2);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
12-26 20:26:55.227 20055 20055 E Dpps    : GetFBNodeIndex():591 Unable to get panel info for node index 0, ret -2
12-26 20:26:55.227 20055 20055 E Dpps    : InitDisplayHardware():255 Unable to get fb node index (-1), display type 0, ret -2
12-26 20:26:55.227 20055 20055 E Dpps    : Init():166 Failed to setup display hardware, ret = -2
12-26 20:26:55.227 20055 20055 E Dpps    : main():29 Failed to initialize DPPS, ret = -2
12-26 20:26:55.227 20055 20055 I Dpps    : DeleteInstance: DppsServer ref count decreased to 0
12-26 20:26:55.227 20055 20055 I Dpps
*/
