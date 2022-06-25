package com.eric.javabean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PaymentStatusUpdateBean {
    PropertyChangeSupport paymentlisteners = new PropertyChangeSupport(this);

    public void updateStatus(int newStatus) {
        // 模拟业务逻辑
        System.out.println("支付状态更新：" + newStatus);

        // 触发通知
        paymentlisteners.firePropertyChange("paymentStatusUpdate", 0, newStatus);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        paymentlisteners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        paymentlisteners.removePropertyChangeListener(listener);
    }
}
