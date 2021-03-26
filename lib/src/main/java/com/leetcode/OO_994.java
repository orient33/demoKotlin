package com.leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//https://leetcode-cn.com/problems/rotting-oranges/
public class OO_994 {

    public static void test() {
        int[][] vv = {
                {2, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 1, 1, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 1, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

        System.out.println("result = " + new OO_994().orangesRotting(vv));
    }

    private static final int NONE = 0;
    private static final int FINE = 1;
    private static final int BAD = 2;

    static class Location {
        final int x, y;

        Location(int a, int b) {
            x = a;
            y = b;
        }

        @Override
        public String toString() {
            return "Location{" + x + "," + y + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x &&
                    y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private final List<Location> seed = new ArrayList<>();

    public int orangesRotting(int[][] grid) {
        int count = 0;
        if (!hasGood(grid)) return 0;
        while (hasGood(grid)) {//若有新鲜的
            count++;
            if (!doStep(grid)) {
                if (hasGood(grid)) {
                    count = -1;
                }
                break;
            }
        }
        return count == 0 ? -1 : count;
    }


    //return if any element changed.
    private boolean doStep(int[][] grid) {
        boolean changed = false;
        //1 找到腐烂的元素
        List<Location> list = new ArrayList<>();
        Location tmp;
        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[i].length; ++j) {
                int e = grid[i][j];
                if (e == BAD) {
                    tmp = new Location(i, j);
                    if (!seed.contains(tmp)) {
                        list.add(new Location(i, j));
                    }
                }
            }
        }
        //2.对腐烂的做扩散
        if (list.size() > 0) {
            for (Location l : list) {
                int i = l.x, j = l.y;
                seed.add(l);
                //top
                if (j - 1 >= 0) {
                    int t = grid[i][j - 1];
                    if (t == FINE) { //新鲜 -> 腐烂
                        grid[i][j - 1] = BAD;
                        changed = true;
                    }
                }
                //bottom
                if (j + 1 < grid[i].length) {
                    if (grid[i][j + 1] == FINE) {
                        grid[i][j + 1] = BAD;
                        changed = true;
                    }
                }
                //left
                if (i - 1 >= 0) {
                    if (grid[i - 1][j] == FINE) {
                        grid[i - 1][j] = BAD;
                        changed = true;
                    }
                }
                //right
                if (i + 1 < grid.length) {
                    if (grid[i + 1][j] == FINE) {
                        grid[i + 1][j] = BAD;
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private boolean hasGood(int[][] grid) {
        for (int[] ints : grid) {
            for (int anInt : ints) {
                if (anInt == FINE) {
                    return true;
                }
            }
        }
        return false;
    }
}
