/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.rocketmq.mq.simple;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2018/12/18 15:31
 * 可靠同步发送在众多场景中被使用,例如重要的通知消息、短信通知、短信营销系统等等
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        producer.start();

        for (int i = 0; i < 100; i++) {
            Message message = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(message);
            System.out.printf("%s%n", sendResult);
        }

        producer.shutdown();
    }
}
