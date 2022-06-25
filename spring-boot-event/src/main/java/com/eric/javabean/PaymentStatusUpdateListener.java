package com.eric.javabean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 支付状态变更的监听器
 */
public class PaymentStatusUpdateListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.printf("支付状态变更, eventName: %s, oldValue: %s, newValue: %s", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
}
