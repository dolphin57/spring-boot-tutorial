package com.eric.loanplan.bank;

public class Banker {
    int[] num_source;//银行家所拥有的资源数
    int[][] max_need_couse;//各进程所需要各资源的最大量
    int[][] have_num;//各进程已拥有各资源的量
    int[][] need_num;//各进程仍需要各资源的量
    int[] p;//已分配出的资源数
    int[] s;//系统剩余资源数

    public Banker(int[] a, int[][] b, int[][] c, int[][] d, int[] p, int[] s) {
        this.num_source = a;
        this.max_need_couse = b;
        this.have_num = c;
        this.need_num = d;
        this.p = p;
        this.s = s;
    }

    public void display() {
        for (int i = 0; i < need_num.length; i++) {
            System.out.println("进程" + i + "仍需要各资源的量为：");
            for (int j = 0; j < need_num[i].length; j++) {
                System.out.print(need_num[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("已分配出的资源数：");
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + " ");
        }
        System.out.println();
        System.out.println("各剩余的资源数：");
        for (int i = 0; i < s.length; i++) {
            System.out.print(s[i] + " ");
            if (s[i] < 0) {
                System.out.println("不安全");
                BankerAlgorithm.set();
            }
        }
        System.out.println();
    }

    public int algorithm(int n, int m, int a) {//银行家算法
        // boolean t = false;
        if (n < max_need_couse.length && m < need_num[0].length && a <= need_num[n][m]) {
            have_num[n][m] += a;
            need_num[n][m] -= a;
            for (int i = 0; i < need_num.length; i++) {
                for (int j = 0; j < need_num[i].length; j++) {
                    if (need_num[i][j] > s[j]) {
                        continue;
                    }
                }
                //t = true;
                return i;
            }
            have_num[n][m] -= a;
            need_num[n][m] += a;

            return -1;
        } else {
            return -2;
        }
    }

    public boolean isNotEnd(int i) {
        if (i < max_need_couse.length) {
            for (int j = 0; j < max_need_couse[0].length; j++) {
                if (have_num[i][j] != max_need_couse[i][j]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean anquan() {
        for (int i = 0; i < max_need_couse.length; i++) {
            for (int j = 0; j < max_need_couse[0].length; j++) {
                if (s[j] < need_num[i][j]) {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.format("E");
        System.out.format("%-20s", "Hello");

        System.out.println(String.format("%-"+20+"s", "dolphin"));

    }
}
