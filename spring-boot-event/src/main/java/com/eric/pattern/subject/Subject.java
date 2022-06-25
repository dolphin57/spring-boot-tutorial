package com.eric.pattern.subject;

import com.eric.pattern.observer.Observer;

/**
 * 被观察者
 */
public interface Subject {
    /**
     * 添加观察者
     * @param observer
     */
    void addObserver(Observer observer);

    /**
     * 删除观察者
     * @param observer
     */
    void removeObserver(Observer observer);

    /**
     * 通知观察者
     */
    void notifyObserver();
}
