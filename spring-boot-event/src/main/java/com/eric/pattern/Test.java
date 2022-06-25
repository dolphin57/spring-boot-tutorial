package com.eric.pattern;

import com.eric.pattern.observer.EmailObserver;
import com.eric.pattern.observer.StockObserver;
import com.eric.pattern.subject.PaymentStatusUpdateSubject;

/**
 * 观察者模式
 */
public class Test {
    public static void main(String[] args) {
        // "支付状态更新"->看做一个事件，可以被监听的事件

        // 被观察者。即事件源
        PaymentStatusUpdateSubject updateSubject = new PaymentStatusUpdateSubject();

        // 观察者。即事件监听器
        EmailObserver emailObserver = new EmailObserver();
        StockObserver stockObserver = new StockObserver();

        // 添加观察者
        updateSubject.addObserver(emailObserver);
        updateSubject.addObserver(stockObserver);

        // 发布事件。支付状态更新
        updateSubject.updatePaymentStatus(2);
    }
}
