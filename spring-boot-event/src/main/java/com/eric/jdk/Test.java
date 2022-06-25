package com.eric.jdk;

public class Test {
    public static void main(String[] args) {
        // "支付状态更新"->看做一个事件，可以被监听的事件

        // 被观察者。即事件源
        PaymentStatusObservable paymentStatusObservable = new PaymentStatusObservable();

        // 添加观察者
        paymentStatusObservable.addObserver(new EmailObserver());
        paymentStatusObservable.addObserver(new StockObserver());

        // 支付状态变更
        paymentStatusObservable.updatePaymentStatus(3);
    }
}
