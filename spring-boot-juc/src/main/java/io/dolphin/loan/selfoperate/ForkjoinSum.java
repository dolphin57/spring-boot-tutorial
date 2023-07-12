package io.dolphin.loan.selfoperate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkjoinSum extends RecursiveTask<Long> {
    private long start;
    private long end;
    public static final int THRESHOLD = 2;

    public ForkjoinSum(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        boolean flag = (end - start) <= THRESHOLD;

        if (flag) {
            for (long i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            long middle = (end + start) / 2;
            ForkjoinSum leftTask = new ForkjoinSum(start, middle);
            ForkjoinSum rightTask = new ForkjoinSum(middle+1, end);

            leftTask.fork();
            rightTask.fork();

            Long left = leftTask.join();
            Long right = rightTask.join();

            sum = left + right;
        }
        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        ForkjoinSum task = new ForkjoinSum(1, 4);
        ForkJoinTask<Long> result = pool.submit(task);

        try {
            Long sum = result.get();
            System.out.println(sum);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
