/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.rocketmq.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2018/12/18 10:56
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        // 需要一个producer group名字作为构造方法参数
        DefaultMQProducer producer = new DefaultMQProducer("producer1");

        // 设置NameServer地址
        producer.setNamesrvAddr("192.168.5.139:9876");
        producer.setVipChannelEnabled(false);

        // 为避免程序启动的时候报错，添加此代码，可以让rocketMq自动创建topickey
        producer.setCreateTopicKey("AUTO_CREATE_TOPIC_KEY");
        producer.start();

        for (int i = 0; i < 10; i++) {
            try {
                Message message = new Message("TopicTest", "Tag1", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(message);
                System.out.println("发送消息的ID:" + sendResult.getMsgId() + "---发送消息的状态:" + sendResult.getSendStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }
}
