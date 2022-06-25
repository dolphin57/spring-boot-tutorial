package com.eric.jdk;

import java.util.Observable;

/**
 * 支付状态变更 作为 可观察者
 */
public class PaymentStatusObservable extends Observable {
    public void updatePaymentStatus(int newStatus) {
        // 业务逻辑操作
        System.out.println("更新新的支付状态为: " + newStatus);

        // 通知观察者
        this.setChanged();//需要调用一下这这方法，表示被观察者的状态已发生变更，Observable才会通知观察者
        this.notifyObservers();
    }
}
