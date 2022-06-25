package com.eric.pattern.subject;

import com.eric.pattern.observer.Observer;

import java.util.List;
import java.util.Vector;

/**
 * 支付状态的被观察者
 */
public class PaymentStatusUpdateSubject implements Subject {

    private List<Observer> observers = new Vector<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public void updatePaymentStatus(int newStatus) {
        // 业务逻辑操作
        System.out.println("更新支付状态：" + newStatus);

        // 通知观察者
        this.notifyObserver();
    }
}
