package com.leetcode;

//机器人大冒险 https://leetcode-cn.com/problems/programmable-robot/
public class OO_LCP_03 {
    public boolean robot(String command, int[][] obstacles, int x, int y) {
        int a = 0, b = 0;
        int max = 100;
        do {
            for (int i = 0; i < command.length(); ++i) {
                if (command.charAt(i) == 'U') {//y
                    a++;
                } else { //R, x轴
                    b++;
                }
                if (containAB(obstacles, a, b)) return false;
                if (x == a && y == b) return true;
                if (a > 1000000000 || b > 1000000000) return false;
            }
        } while (--max > 0);
        return false;
    }

    private boolean containAB(int[][] obs, int a, int b) {
        for (int[] ob : obs) {
            int v = ob[0], w = ob[1];
            if (a == v && w == b) {
                return true;
            }
        }
        return false;
    }
}
