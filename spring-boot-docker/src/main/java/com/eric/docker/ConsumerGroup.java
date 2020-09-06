/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.docker;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2019-1-24 18:54
 */
public class ConsumerGroup {
    private String groupId;

    public ConsumerGroup() {
    }

    public ConsumerGroup(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
