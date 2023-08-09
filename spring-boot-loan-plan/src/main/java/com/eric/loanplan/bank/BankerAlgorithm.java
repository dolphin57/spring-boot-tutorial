package com.eric.loanplan.bank;

import java.util.Scanner;

public class BankerAlgorithm {
    public static void set() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入资源数:");
        int a = scanner.nextInt();
        System.out.println("请输入进程数:");
        int b = scanner.nextInt();

        int[] source = new int[a]; // 资源数
        System.out.println("请分别输入各资源的拥有总量");
        for (int i = 0; i < a; i++) {
            int a_i = scanner.nextInt();
            source[i] = a_i;
        }

        int[][] couse = new int[b][a];//进程对资源的最大需求量
        for (int i = 0; i < b; i++) {
            System.out.println("请分别输入进程" + i + "对各资源的需求总量");
            for (int j = 0; j < a; j++) {
                //System.out.println("请输入进程"+i+"对资源"+j+"的最大需求量：");
                int b_ij = scanner.nextInt();
                couse[i][j] = b_ij;
            }
        }

        int[][] couse1 = new int[b][a];
        for (int i = 0; i < b; i++) {
            System.out.println("请分别输入进程" + i + "各资源的已分配量");
            for (int j = 0; j < a; j++) {
                //System.out.println("请输入进程"+i+"对资源"+j+"的已分配量：");
                int b_ij = scanner.nextInt();
                couse1[i][j] = b_ij;
            }
        }

        int[][] couse2 = new int[b][a];
        for (int i = 0; i < b; i++) {
            for (int j = 0; j < a; j++) {
                couse2[i][j] = couse[i][j] - couse1[i][j];
            }
        }

        int[] p = new int[a];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                p[i] += couse1[j][i];
            }
        }

        int[] s = new int[a];
        for (int i = 0; i < a; i++) {
            s[i] = source[i] - p[i];
        }

        Banker b1 = new Banker(source,couse,couse1,couse2,p,s);
        b1.display();

    }
}
