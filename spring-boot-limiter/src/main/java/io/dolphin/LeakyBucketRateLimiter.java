package io.dolphin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dolphin
 * @date 2023年12月28日 16:44
 * @description
 */
public class LeakyBucketRateLimiter {
    Logger logger = LoggerFactory.getLogger(LeakyBucketRateLimiter.class);

    int capacity;
    AtomicInteger water = new AtomicInteger();
    long leakTimestamp;
    int leakRate;

    public LeakyBucketRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
    }
    public synchronized boolean tryAcquire() {
        if (water.get() == 0) {
            logger.info("start leaking");
            leakTimestamp = System.currentTimeMillis();
            water.incrementAndGet();
            return water.get() < capacity;
        }

        //先漏水，计算剩余水量
        long currentTime = System.currentTimeMillis();
        int leakedWater = (int) ((currentTime - leakTimestamp) / 1000 * leakRate);
        logger.info("lastTime:{}, currentTime:{}. LeakedWater:{}", leakTimestamp, currentTime, leakedWater);

        if (leakedWater != 0) {
            int leftWater = water.get() - leakedWater;
            //可能水已漏光。设为0
            water.set(Math.max(0, leftWater));
            leakTimestamp = System.currentTimeMillis();
        }

        logger.info("剩余容量:{}", capacity - water.get());
        if (water.get() < capacity) {
            logger.info("tryAcquire sucess");
            water.incrementAndGet();
            return true;
        } else {
            logger.info("tryAcquire fail");
            return false;
        }
    }

    public static void main(String[] args) {
        LeakyBucketRateLimiter leakyBucketRateLimiter = new LeakyBucketRateLimiter(10, 1);
        for (int i = 0; i < 15; i++) {
            boolean b = leakyBucketRateLimiter.tryAcquire();
            System.out.println(b);
        }
    }
}
