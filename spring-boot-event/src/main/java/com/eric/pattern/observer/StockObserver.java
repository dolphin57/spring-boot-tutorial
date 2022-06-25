package com.eric.pattern.observer;

/**
 * 库存服务
 */
public class StockObserver implements Observer {
    @Override
    public void update() {
        System.out.println("库存服务收到通知");
    }
}
