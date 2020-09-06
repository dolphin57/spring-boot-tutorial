/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.rocketmq.mq.simple;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2018/12/18 15:46
 * 单向传输用于需要中等可靠性的情况，例如日志收集.
 */
public class OnewayProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        producer.start();

        for (int i = 0; i < 100; i++) {
            Message message = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.sendOneway(message);
        }

        producer.shutdown();
    }
}
