package com.eric.jdk;

import java.util.Observable;
import java.util.Observer;

/**
 * 邮件服务
 */
public class EmailObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("邮件服务收到通知");
    }
}
