package com.eric.loanplan.bank;

import java.util.*;

/**
 * 银行家算法
 */
public class BankersAlgorithm {
    // 进程数
    private Integer processCount;

    // 资源数
    private Integer resourceCount;

    // 可用资源
    private static List<Integer> availableResource;

    // 最大资源矩阵
    private static List<List<Integer>> maxResource;

    // 可允许资源矩阵
    private static List<List<Integer>> allocatedResource;

    // 所需矩阵
    private static List<List<Integer>> neededResource;

    /**
     * 初始化数据结构
     */
//    void initializeData(int numProcesses, int numResources, int[] avail, int[][] max, int[][] alloc) {
//        this.processCount = numProcesses;
//        this.resourceCount = numResources;
//        this.availableResource = avail;
//        this.maxResource = max;
//        this.allocatedResource = alloc;
//
//        // Initialize the need matrix
//        this.neededResource = new int[processCount][resourceCount];
//        for (int i = 0; i < processCount; i++) {
//            for (int j = 0; j < resourceCount; j++) {
//                neededResource[i][j] = maxResource[i][j] - allocatedResource[i][j];
//            }
//        }
//    }

//    boolean isSafe() {
//        int[] work = new int[resourceCount];
//        boolean[] finish = new boolean[processCount];
//
//        for (int i = 0; i < resourceCount; i++)
//            work[i] = availableResource[i];
//
//        for (int i = 0; i < processCount; i++)
//            finish[i] = false;
//
//        while (true) {
//            boolean found = false;
//            for (int p = 0; p < processCount; p++) {
//                if (finish[p] == false) {
//                    int j;
//                    for (j = 0; j < resourceCount; j++)
//                        if (neededResource[p][j] > work[j])
//                            break;
//                    if (j == resourceCount) {
//                        for (int k = 0; k < resourceCount; k++)
//                            work[k] += allocatedResource[p][k];
//                        finish[p] = true;
//                        found = true;
//                    }
//                }
//            }
//
//            if (found == false)
//                break;
//        }
//
//        for (int i = 0; i < processCount; i++)
//            if (finish[i] == false)
//                return false;
//
//        return true;
//    }

//    void requestResources(int process, int[] request) {
//        // Check if request <= need
//        for (int i = 0; i < resourceCount; i++) {
//            if (request[i] > neededResource[process][i]) {
//                System.out.println("Error: Process has exceeded its maximum claim.");
//                return;
//            }
//        }
//
//        // Check if request <= available
//        for (int i = 0; i < resourceCount; i++) {
//            if (request[i] > availableResource[i]) {
//                System.out.println("Resources are not available. Process needs to wait.");
//                return;
//            }
//        }
//
//        // Temporarily allocate resources
//        for (int i = 0; i < resourceCount; i++) {
//            availableResource[i] -= request[i];
//            allocatedResource[process][i] += request[i];
//            neededResource[process][i] -= request[i];
//        }
//
//        // Check if system is still in safe state
//        if (isSafe()) {
//            System.out.println("Resources have been allocated.");
//        } else {
//            // If not safe, release resources
//            for (int i = 0; i < resourceCount; i++) {
//                availableResource[i] += request[i];
//                allocatedResource[process][i] -= request[i];
//                neededResource[process][i] += request[i];
//            }
//            System.out.println("System is not in safe state. Resources have been released.");
//        }
//    }
    public static void main(String[] args) {
        while (true) {
            System.out.println("           银行家算法：       \n");
            System.out.println("*****  1 - 初始化各矩阵        *****");
            System.out.println("*****  2 - 进程提出请求        *****");
            System.out.println("*****  3 - 显示各进程资源情况   *****");
            System.out.println("*****  0 - 结束              *****\n");
            System.out.print("输入你的选择 ： ");

            Scanner scanner = new Scanner(System.in);
            int choose = scanner.nextInt();

            switch (choose) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    init();
                    break;
                case 2:
                    require();
                    break;
                case 3:
                    display();
                    break;
                default:
                    System.out.println("请输入正确的序号：\\n");
            }
        }
    }

    private static void init() {
        System.out.print("输入进程的个数：");
        Scanner scanner = new Scanner(System.in);
        int processCount = scanner.nextInt();
        System.out.print("输入资源的种类：");
        int resourceCount = scanner.nextInt();

        System.out.print("输入系统剩余资源(Available):");
        for (int i = 0; i < resourceCount; i++) {
            int tmpAvail = scanner.nextInt();
            availableResource.add(tmpAvail);
        }
        System.out.println();

        System.out.println("输入Allocation矩阵");
        for (int i = 0; i < processCount; i++) {
            for (int j = 0; j < resourceCount; j++) {
                int tmpAllocation = scanner.nextInt();
                allocatedResource.get(i).add(tmpAllocation);
                maxResource.get(i).add(tmpAllocation);
            }
        }
        System.out.println();

        System.out.println("输入Need矩阵");
        for (int i = 0; i < processCount; i++) {
            for (int j = 0; j < resourceCount; j++) {
                int tmpNeed = scanner.nextInt();
                neededResource.get(i).add(tmpNeed);
                maxResource.get(i).add(j, maxResource.get(i).get(j) + tmpNeed);
            }
        }
        System.out.println("\n初始化完毕!\n\n");

        System.out.println("检测系统是否安全.......\n");
        Pair<List<Integer>, BankStatus> safe = isSafe();
        if (safe.getSecond() == BankStatus.SECURITY) {
            System.out.println("系统此时为安全状态\n");
            display();
        } else {
            System.out.println("系统此时处于不安全状态，请检查你的数据是否合理\n");
        }
    }

    private static Pair<List<Integer>, BankStatus> isSafe() {
        Pair<List<Integer>, BankStatus> retval = new Pair<>(new ArrayList<Integer>(), BankStatus.UNSECURITY);

        // 1. 设置work和finish变量
        // 系统可提供给进程继续运行所需的各类资源数目, 工作数组
        List<Integer> work = new ArrayList<>(availableResource);
        // 系统是否有足够的资源分配给进程, 可分配标志
        List<Boolean> finish = new ArrayList<>(neededResource.size());
        for (int i = 0; i < neededResource.size(); i++) {
            finish.add(false);
        }

        int flag; // 每找到一个进程满足安全状态，flag--，减到0说明系统出于安全状态
        int tag;

        // 2. 找到符合条件的进程
        while (true) {
            // 指定false在finish集合出现的次数 flag记录暂时不能分配的进程个数
            flag = Collections.frequency(finish, false);
            tag = flag; // 记录flag大小

            // 遍历找出进程符合 finish[i]=false & need[i,j] <= work[j]
            for (int i = 0; i < neededResource.size(); i++) {
                if (compareArrays(neededResource.get(i), work) && !finish.get(i)) {
                    // 3. 将分配到的资源allocation给进程request[i]
                    for (int j = 0; j < work.size(); j++) {
                        work.set(j, work.get(j) + allocatedResource.get(i).get(j));
                    }
                    finish.set(i, true); // 分配进程完成设置为true
                    flag--;
                    retval.getFirst().add(i); // 将进程id push进安全序列中
                }
            }

            // 4. 所有进程Finish==true,表示系统安全
            if (flag == 0) {
                retval.setSecond(BankStatus.SECURITY);
                break;
            }

            // tag=flag表示没有一个finish为true，资源都在被占用，则进入死锁，不安全
            if (tag == flag) {
                retval.setSecond(BankStatus.UNSECURITY);
                break;
            } else {
                // 循环查找
            }
        }
        return retval;
    }

    private static boolean compareArrays(List<Integer> array1, List<Integer> array2) {
        if (array1.size() != array2.size()) {
            return false;
        }
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i) > array2.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static void display() {
        String name = "ABCDEFGH";
        int processCount = neededResource.size();
        int resourceCount = neededResource.get(0).size();

        //1.打印表头
        System.out.println("|—————————————————————————————————————————-|");
        System.out.printf("%-8s%-19s%-19s%-19s%-20s", "|进\\资源", "|       Max        ", "|    Allocation    ", "|       Need       ", "|     Available    |\n");
        System.out.println("|   \\情 |—————————|—————————|—————————|—————————|");
        System.out.println("|程  \\况");

        for (int i = 0; i < 4; i++) {
            System.out.print("|");
            for (int j = 0; j < resourceCount; j++) {
                System.out.printf(" %c ", name.charAt(j));
            }
            int tmp = 6 - resourceCount;
            System.out.println(String.format("%-"+tmp * 3+"s", " "));
        }
        System.out.println("|\n");
        System.out.println("|-------|—————————|—————————|—————————|—————————|");

        //2.打印每一行
        for (int i = 0; i < processCount; i++) {
            System.out.printf("|  P%-2d  ", i);
            for (int j = 0; j < resourceCount; j++) {//Max数据
                System.out.print("|");
                for (j = 0; j < resourceCount; j++) {
                    System.out.printf(" %d ", maxResource.get(i).get(j));
                }
                int tmp = 6 - resourceCount;//超出的用空格弥补
                System.out.println(String.format("%-"+tmp * 3+"s", " "));
            }

            for (int j = 0; j < resourceCount; j++) {//Allcoation数据
                System.out.print("|");
                for (j = 0; j < resourceCount; j++) {
                    System.out.printf(" %d ", allocatedResource.get(i).get(j));
                }
                int tmp = 6 - resourceCount;//超出的用空格弥补
                System.out.println(String.format("%-"+tmp * 3+"s", " "));
            }

            for (int j = 0; j < resourceCount; j++) {//Need数据
                System.out.print("|");
                for (j = 0; j < resourceCount; j++) {
                    System.out.printf(" %d ", neededResource.get(i).get(j));
                }
                int tmp = 6 - resourceCount;//超出的用空格弥补
                System.out.println(String.format("%-"+tmp * 3+"s", " "));
            }

            System.out.print("|");
            for (int j = 0; j < resourceCount; j++) {//Available数据
                System.out.printf(" %d ", availableResource.get(j));
            }

            int tmp = 6 - resourceCount;//超出的用空格弥补
            System.out.println(String.format("%-"+tmp * 3+"s", " "));

            System.out.println("|\n");
            System.out.println("|-------|—————————|—————————|—————————|—————————|");
        }
    }

    private static void require() {
        List<Integer> request = new ArrayList<>();
        Pair<List<Integer>, BankStatus> retval;
        Scanner scanner = new Scanner(System.in);

        System.out.println("输入要想系统请求资源的进程Id：");
        int pid = scanner.nextInt();

        System.out.println("输入要想向系统请求资源的大小：");
        for (int i = 0; i < availableResource.size(); i++) {
            int tmp = scanner.nextInt();
            request.add(tmp);
        }

        retval = algoBanker(pid, request);
        switch (retval.getSecond()) {
            case NEED_REQUEST:
                System.out.println("\n请求出错！此进程请求资源超过它宣布的最大需求！！！\n");
                System.out.println("当前时刻资源分配表");
                display();
                break;
            case AVAILABLE_REQUEST:
                System.out.println("\n请求出错！此请求所需资源超过系统资源,P" + pid + "进程等待！！！\n");
                System.out.println("当前时刻资源分配表");
                display();
                break;
            case SUCCESS:
                System.out.println("\n预分配成功！\n");
                displaySecurity(retval);
                System.out.println("当前时刻资源分配表");
                display();
                break;
            case FAIL:
                System.out.println("\n分配失败！此次分配会导致系统进出入不安全状态！！！\n");
                displaySecurity(retval);
                System.out.println("当前时刻资源分配表");
                display();
                break;
        }
    }

    private static Pair<List<Integer>, BankStatus> algoBanker(int pid, List<Integer> requset) {
        Pair<List<Integer>, BankStatus> retval = new Pair<>(new ArrayList<>(), BankStatus.FAIL);
        if (!isLessThanOrEqual(requset, neededResource.get(pid))) {
            retval.setSecond(BankStatus.NEED_REQUEST);
        } else if (!isLessThanOrEqual(requset, availableResource)) {
            retval.setSecond(BankStatus.AVAILABLE_REQUEST);
        } else {
            // 以可用资源数进行遍历, 进行预分配操作
            for (int i = 0; i < availableResource.size(); i++) {
                availableResource.set(i, availableResource.get(i) - requset.get(i));
                allocatedResource.get(pid).set(i, allocatedResource.get(pid).get(i) + requset.get(i));
                neededResource.get(pid).set(i, neededResource.get(pid).get(i) - requset.get(i));
            }
            System.out.println("预分配状态表：\n");
            display();
            // 调用安全算法,
            retval = isSafe();

            if (retval.getSecond() == BankStatus.SECURITY) {
                if (neededResource.get(pid).stream().allMatch(x -> x == 0)) {
                    for (int i = 0; i < availableResource.size(); i++) {
                        availableResource.set(i, availableResource.get(i) + allocatedResource.get(pid).get(i));
                        allocatedResource.get(pid).set(i, 0);
                    }
                } else {

                }
                retval.setSecond(BankStatus.SUCCESS);
            } else {
                // 不安全则回收资源
                for (int i = 0; i < availableResource.size(); i++) {
                    availableResource.set(i, availableResource.get(i) + requset.get(i));
                    allocatedResource.get(pid).set(i, allocatedResource.get(pid).get(i) - requset.get(i));
                    neededResource.get(pid).set(i, neededResource.get(pid).get(i) + requset.get(i));
                }
                retval.setSecond(BankStatus.FAIL);
            }
        }
        return retval;
    }

    private static boolean isLessThanOrEqual(List<Integer> aList, List<Integer> bList) {
        for (int i = 0; i < aList.size(); i++) {
            if (aList.get(i) > bList.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static void displaySecurity(Pair<List<Integer>, BankStatus> tmp) {
        if (tmp.getSecond() == BankStatus.SUCCESS) {
            System.out.print("安全序列为：");
            for (Integer e : tmp.getFirst()) {
                System.out.print("P" + e + "->");
            }
            System.out.println();
        } else {
            if (tmp.getFirst().size() > 0) {
                for (Integer e : tmp.getFirst()) {
                    System.out.print("P" + e + "->");
                }
                System.out.println("找到P" + tmp.getFirst().get(tmp.getFirst().size() - 1) + "进程后再找不到安全序列！");
            }
        }
    }
}
