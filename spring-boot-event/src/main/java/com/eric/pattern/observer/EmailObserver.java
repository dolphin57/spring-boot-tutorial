package com.eric.pattern.observer;

/**
 * 邮件服务
 */
public class EmailObserver implements Observer {
    @Override
    public void update() {
        System.out.println("邮件服务收到通知");
    }
}
