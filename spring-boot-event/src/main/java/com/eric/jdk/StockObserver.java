package com.eric.jdk;

import java.util.Observable;
import java.util.Observer;

/**
 * 库存服务
 */
public class StockObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("库存服务收到通知");
    }
}
