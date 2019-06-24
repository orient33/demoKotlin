package com.mioj;

import java.util.Scanner;

/* //https://code.mi.com/problem/list/view?id=1
描述
和所有的 OJ 平台一样，第一题作为热身题，也是送分题：给出两个非负数 aa 和 bb，输出 a+ba+b 的结果。

输入
多组输入。

包含两个非负数 aa 和 bb，以空格分隔；aa 和 bb 保证小于 2^{32}2
32
 .

输出
你的输出是对一行数据处理的结果，也即 a+ba+b 的结果。

输入样例
233 666
123 0

输出样例
899
123
*/
public class Id_1 {
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        String line;
        String[] array;
        while (scan.hasNextLine()) {
            line = scan.nextLine().trim();
            array = line.split(" ");
            long sum = 0;
            if (array.length != 2) continue;
            for (int i = 0; i < array.length; i++) {
                try {
                    long a = Long.parseLong(array[i]);
                    sum = sum + a;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            System.out.println(sum);
        }
        System.out.println("------end------");
    }
}
