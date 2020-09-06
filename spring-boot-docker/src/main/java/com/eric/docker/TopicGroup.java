/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.docker;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2019-1-24 18:56
 */
public class TopicGroup {
    private String topicName;

    public TopicGroup() {
    }

    public TopicGroup(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
