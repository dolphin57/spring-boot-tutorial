package com.eric.spring;

import org.springframework.context.ApplicationEvent;

/**
 * 支付状态更新事件
 */
public class PaymentStatusUpdateEvent extends ApplicationEvent {
    public PaymentStatusUpdateEvent(Object source) {
        super(source);
    }
}
