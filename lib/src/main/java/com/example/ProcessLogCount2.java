package com.example;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.example.MyClass.log;

/**
 * https://jira.n.xiaomi.com/browse/MIFG-1884
 * 分析log的时间断层, 找出开机时间
 */
class ProcessLogCount2 {
    static void count() throws FileNotFoundException {
        File file = new File("D:\\bug-an\\1884-bugreport-2019-05-27-105153\\bugreport-cepheus-PKQ1.181121.001-2019-05-27-10-51-53.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<Integer, String> map = new HashMap<>(100);//key 行号, value,间隔分钟数
        int lineNumber = 0;
        String line, subLine;
        int minute = -1;
        try {
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (!line.startsWith("05-2")) {
//                    log("line too short : " + line);
                    continue;
                }
                if (line.length() > 20 && line.indexOf(18) == ' ') {
                    continue;
                }
                subLine = line.substring(9, 11);
                int now_minute = Integer.valueOf(subLine);
                if (minute == -1) {
                    minute = now_minute;
                } else {
                    if (now_minute == 0) now_minute = 60;
                    int inteval = now_minute - minute;
                    if (inteval > 31) {
                        map.put(lineNumber, inteval + "-" + line);
                    }
                    minute = now_minute;
                }
            }
            map.forEach((integer, integer2) -> {
                log("lineNumber : " + integer + ", inteval :" + integer2);
            });
            log("lineNumber is =" + lineNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
05-26 00:32:53.597
*/
