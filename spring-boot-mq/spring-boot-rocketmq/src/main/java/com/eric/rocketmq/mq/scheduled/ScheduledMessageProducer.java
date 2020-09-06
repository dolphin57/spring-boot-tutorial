/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.rocketmq.mq.scheduled;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2018/12/19 10:41
 */
public class ScheduledMessageProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        // 设置NameServer地址
        producer.setNamesrvAddr("192.168.5.139:9876");
        producer.setVipChannelEnabled(false);
        producer.start();

        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("TopicTest", ("Hello scheduled message " + i).getBytes());
            message.setDelayTimeLevel(3);
            producer.send(message);
        }

        producer.shutdown();
    }
}
